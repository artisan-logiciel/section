package backend.http

import backend.Server.Log.log
import backend.domain.Account
import backend.domain.AccountPassword
import backend.domain.KeyAndPassword
import backend.domain.PasswordChange
import backend.http.problems.EmailAlreadyUsedProblem
import backend.http.problems.InvalidPasswordProblem
import backend.repositories.entities.User
import backend.services.MailService
import backend.services.SecurityUtils.getCurrentUserLogin
import backend.services.UserService
import backend.services.exceptions.EmailAlreadyUsedException
import backend.services.exceptions.InvalidPasswordException
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder.*
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("api")
class AccountController(
    private val userService: UserService,
    private val mailService: MailService
) {
    internal class AccountResourceException(message: String) : RuntimeException(message)

    /**
     * {@code POST  /register} : register the user.
     *
     * @param account the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedProblem {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedBadRequestException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("register")
    @ResponseStatus(CREATED)
    suspend fun registerAccount(
        @Valid @RequestBody account: AccountPassword
    ): Account = Account(
        userService.register(account.apply {
            InvalidPasswordException().apply {
                if (isPasswordLengthInvalid(password!!)) throw this
            }
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
        userService.activateRegistration(key = key).run {
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
    suspend fun getAccount(): Account = log.info("controller getAccount").run {
        userService.getUserWithAuthorities().run<User?, Nothing> {
            if (this == null) throw AccountResourceException("User could not be found")
            else return Account(user = this)
        }
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param account the current user information.
     * @throws EmailAlreadyUsedProblem {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException          {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("account")
    suspend fun saveAccount(@Valid @RequestBody account: Account): Unit {
        getCurrentUserLogin().apply principal@{
            if (isBlank()) throw AccountResourceException("Current user login not found")
            else {
                userService.findAccountByEmail(account.email!!).apply {
                    if (!this?.login?.equals(this@principal, true)!!)
                        throw EmailAlreadyUsedException()
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
     * @throws InvalidPasswordProblem {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping("account/change-password")
    suspend fun changePassword(@RequestBody passwordChange: PasswordChange): Unit =
        passwordChange.run {
            InvalidPasswordException().apply { if (isPasswordLengthInvalid(newPassword)) throw this }
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
     * @throws InvalidPasswordProblem {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException         {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping("account/reset-password/finish")
    suspend fun finishPasswordReset(@RequestBody keyAndPassword: KeyAndPassword): Unit {
        keyAndPassword.run {
            InvalidPasswordException().apply { if (isPasswordLengthInvalid(newPassword)) throw this }
            if (newPassword != null && key != null)
                if (userService.completePasswordReset(newPassword, key) == null)
                    throw AccountResourceException("No user was found for this reset key")
        }
    }
}
