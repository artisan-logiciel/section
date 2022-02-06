package backend.http

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Order
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder.fromHttpRequest
import backend.config.Constants.LOGIN_REGEX
import backend.config.Constants.ROLE_ADMIN
import backend.Server.Log.log
import backend.domain.Account
import backend.http.util.HttpHeaderUtil.createAlert
import backend.http.util.PaginationUtil.generatePaginationHttpHeaders
import backend.http.problems.AlertProblem
import backend.http.problems.EmailAlreadyUsedProblem
import backend.http.problems.LoginAlreadyUsedProblem
import backend.properties.ApplicationProperties
import backend.repositories.entities.User
import backend.services.MailService
import backend.services.UserService
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import javax.validation.constraints.Pattern

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the {@link User} entity, and needs to fetch its collection of authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping("api/admin")
class UserController(
    private val userService: UserService,
    private val mailService: MailService,
    private val properties: ApplicationProperties
) {
    companion object {
        private val ALLOWED_ORDERED_PROPERTIES =
            arrayOf(
                "id",
                "login",
                "firstName",
                "lastName",
                "email",
                "activated",
                "langKey"
            )
    }

    /**
     * {@code POST  /admin/users}  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param account the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user,
     * or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws AlertProblem {@code 400 (Bad Request)} if the login or email is already in use.
     */
    @PostMapping("users")
    @PreAuthorize("hasAuthority(\"$ROLE_ADMIN\")")
    suspend fun createUser(@Valid @RequestBody account: Account): ResponseEntity<User> {
        account.apply requestAccount@{
            log.debug("REST request to save User : {}", account)
            if (id != null) throw AlertProblem(
                defaultMessage = "A new user cannot already have an ID",
                entityName = "userManagement",
                errorKey = "idexists"
            )
            userService.findAccountByLogin(login!!).apply retrieved@{
                if (this@retrieved?.login?.equals(
                        this@requestAccount.login,
                        true
                    ) == true
                ) throw LoginAlreadyUsedProblem()
            }
            userService.findAccountByEmail(email!!).apply retrieved@{
                if (this@retrieved?.email?.equals(
                        this@requestAccount.email,
                        true
                    ) == true
                ) throw EmailAlreadyUsedProblem()
            }
            userService.createUser(this).apply {
                mailService.sendActivationEmail(this)
                try {
                    return created(URI("/api/admin/users/$login"))
                        .headers(
                            createAlert(
                                properties.clientApp.name,
                                "userManagement.created",
                                login
                            )
                        ).body(this)
                } catch (e: URISyntaxException) {
                    throw RuntimeException(e)
                }
            }
        }
    }

    /**
     * {@code PUT /admin/users} : Updates an existing User.
     *
     * @param account the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user.
     * @throws EmailAlreadyUsedProblem {@code 400 (Bad Request)} if the email is already in use.
     * @throws LoginAlreadyUsedProblem {@code 400 (Bad Request)} if the login is already in use.
     */
    @PutMapping("/users")
    @PreAuthorize("hasAuthority(\"$ROLE_ADMIN\")")
    suspend fun updateUser(@Valid @RequestBody account: Account): ResponseEntity<Account> {
        log.debug("REST request to update User : {}", account)
        userService.findAccountByEmail(account.email!!).apply {
            if (this == null) throw ResponseStatusException(NOT_FOUND)
            if (id != account.id) throw EmailAlreadyUsedProblem()
        }
        userService.findAccountByLogin(account.login!!).apply {
            if (this == null) throw ResponseStatusException(NOT_FOUND)
            if (id != account.id) throw LoginAlreadyUsedProblem()
        }
        return ok()
            .headers(
                createAlert(
                    properties.clientApp.name,
                    "userManagement.updated",
                    account.login
                )
            ).body(userService.updateUser(account))
    }

    /**
     * {@code GET /admin/users} : get all users with all the details -
     * calling this are only allowed for the administrators.
     *
     * @param request a {@link ServerHttpRequest} request.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/users")
    @PreAuthorize("hasAuthority(\"$ROLE_ADMIN\")")
    suspend fun getAllUsers(request: ServerHttpRequest, pageable: Pageable): ResponseEntity<Flow<Account>> =
        log.debug("REST request to get all User for an admin").run {
            return if (!onlyContainsAllowedProperties(pageable)) {
                badRequest().build()
            } else ok()
                .headers(
                    generatePaginationHttpHeaders(
                        fromHttpRequest(request),
                        PageImpl<Account>(
                            mutableListOf(),
                            pageable,
                            userService.countUsers()
                        )
                    )
                ).body(userService.getAllManagedUsers(pageable))
        }


    private fun onlyContainsAllowedProperties(pageable: Pageable): Boolean = pageable
        .sort
        .stream()
        .map(Order::getProperty)
        .allMatch(ALLOWED_ORDERED_PROPERTIES::contains)


    /**
     * {@code GET /admin/users/:login} : get the "login" user.
     *
     * @param login the login of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}
     * and with body the "login" user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/users/{login}")
    @PreAuthorize("hasAuthority(\"$ROLE_ADMIN\")")
    suspend fun getUser(@PathVariable login: String): Account =
        log.debug("REST request to get User : {}", login).run {
            return Account(userService.getUserWithAuthoritiesByLogin(login).apply {
                if (this == null) throw ResponseStatusException(NOT_FOUND)
            }!!)
        }

    /**
     * {@code DELETE /admin/users/:login} : delete the "login" User.
     *
     * @param login the login of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/users/{login}")
    @PreAuthorize("hasAuthority(\"$ROLE_ADMIN\")")
    @ResponseStatus(code = NO_CONTENT)
    suspend fun deleteUser(
        @PathVariable @Pattern(regexp = LOGIN_REGEX) login: String
    ): ResponseEntity<Unit> {
        log.debug("REST request to delete User: {}", login).run {
            userService.deleteUser(login).run {
                return noContent().headers(
                    createAlert(
                        properties.clientApp.name,
                        "userManagement.deleted",
                        login
                    )
                ).build()
            }
        }
    }
}
