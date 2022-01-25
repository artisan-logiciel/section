package reaktive.http

import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.apache.commons.lang3.StringUtils
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder.*
import reaktive.config.Constants
import reaktive.config.Log.log
import reaktive.domain.Account
import reaktive.domain.AccountPassword
import reaktive.domain.KeyAndPassword
import reaktive.domain.PasswordChange
import reaktive.http.problems.EmailAlreadyUsedBadRequestException
import reaktive.http.problems.InvalidPasswordBadRequestException
import reaktive.repositories.entities.User
import reaktive.services.MailService
import reaktive.services.SecurityUtils.getCurrentUserLogin
import reaktive.services.UserService
import reaktive.services.exceptions.InvalidPasswordException
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("api")
class AccountController(
    private val userService: UserService,
    private val mailService: MailService
) {
    internal class AccountResourceException(message: String) : RuntimeException(message)

    fun isPasswordLengthInvalid(password: String?): Boolean =
        if (StringUtils.isEmpty(password)) false
        else password?.length!! < Constants.PASSWORD_MIN_LENGTH ||
                password.length > Constants.PASSWORD_MAX_LENGTH

    /**
     * {@code POST  /register} : register the user.
     *
     * @param account the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedBadRequestException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedBadRequestException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("register")
    @ResponseStatus(CREATED)
    suspend fun registerAccount(
        @Valid @RequestBody account: AccountPassword
    ): Account = Account(
        userService.register(account.apply {
            if (isPasswordLengthInvalid(password!!)) throw InvalidPasswordException()
        }, account.password!!)
            ?.also {
                if (!userService.getUserWithAuthoritiesByLogin(account.email!!)
                        ?.activationKey
                        .isNullOrBlank()
                ) mailService.sendActivationEmail(it)
            }!!
    )

    /**
     * `GET  /activate` : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException `500 (Internal Server Error)` if the user couldn't be activated.
     */
    @GetMapping("/activate")
    suspend fun activateAccount(@RequestParam(value = "key") key: String): Unit =
        userService.activateRegistration(key).run {
            if (this == null) throw AccountResourceException("No user was found for this activation key")
        }

    /**
     * `GET  /authenticate` : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    suspend fun isAuthenticated(request: ServerWebExchange): String? =
        request.getPrincipal<Principal>().map(Principal::getName).awaitFirstOrNull().also {
            log.debug("REST request to check if the current user is authenticated")
        }


    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("account")
    suspend fun getAccount(): Account =
        log.info("controller getAccount").run {
            userService
                .getUserWithAuthorities()
                .run<User?, Nothing> {
                    log.info("depuis le controller voici le user: $this")
                    return if (this == null)
                        throw AccountResourceException("User could not be found")
                    else Account(user = this)
                }
        }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param account the current user information.
     * @throws EmailAlreadyUsedBadRequestException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException          {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("account")
    suspend fun saveAccount(@Valid @RequestBody account: Account): Unit {
        getCurrentUserLogin().apply principal@{
            if (isBlank()) throw AccountResourceException("Current user login not found")
            else {
                userService.findAccountByEmail(account.email!!).apply {
                    if (!this?.login?.equals(this@principal, true)!!)
                        throw EmailAlreadyUsedBadRequestException()
                }
                userService.findAccountByLogin(account.login!!).apply {
                    if (this == null)
                        throw AccountResourceException("User could not be found")
                }
                userService.updateUser(
                    account.firstName,
                    account.lastName,
                    account.email,
                    account.langKey,
                    account.imageUrl
                )
            }
        }
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChange current and new password.
     * @throws InvalidPasswordBadRequestException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping("account/change-password")
    suspend fun changePassword(@RequestBody passwordChange: PasswordChange): Unit =
        passwordChange.run {
            if (isPasswordLengthInvalid(newPassword)) throw InvalidPasswordBadRequestException()
            if (currentPassword != null && newPassword != null)
                userService.changePassword(currentPassword, newPassword)
        }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     */
    @PostMapping("account/reset-password/init")
    suspend fun requestPasswordReset(@RequestBody mail: String): Unit =
        userService.requestPasswordReset(mail).run {
            if (this == null) log.warn("Password reset requested for non existing mail")
            else mailService.sendPasswordResetMail(this)
        }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordBadRequestException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException         {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping("account/reset-password/finish")
    suspend fun finishPasswordReset(@RequestBody keyAndPassword: KeyAndPassword): Unit {
        keyAndPassword.run {
            if (isPasswordLengthInvalid(newPassword))
                throw InvalidPasswordBadRequestException()
            if (newPassword != null && key != null)
                if (userService.completePasswordReset(newPassword, key) == null)
                    throw AccountResourceException("No user was found for this reset key")
        }
    }
}
