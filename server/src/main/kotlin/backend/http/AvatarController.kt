package backend.http

import backend.Server.Log.log
import backend.domain.Avatar
import backend.http.util.PaginationUtil.generatePaginationHttpHeaders
import backend.services.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toCollection
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Order
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.badRequest
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder.fromHttpRequest

@RestController
@RequestMapping("/api")
class AvatarController(
    private val userService: UserService
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
     * {@code GET /users} : get all users with only the public informations - calling this are allowed for anyone.
     *
     * @param request a {@link ServerHttpRequest} request.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/users")
    suspend fun getAllAvatars(
        request: ServerHttpRequest,
        pageable: Pageable
    ): ResponseEntity<Flow<Avatar>> = log
        .debug("REST request to get all public User names").run {
            return if (!onlyContainsAllowedProperties(pageable)) badRequest().build()
            else {
                ok().headers(
                    generatePaginationHttpHeaders(
                        fromHttpRequest(request),
                        PageImpl<Avatar>(
                            mutableListOf(),
                            pageable,
                            userService.countUsers()
                        )
                    )
                ).body(userService.getAllPublicUsers(pageable))
            }
        }

    private fun onlyContainsAllowedProperties(
        pageable: Pageable
    ): Boolean = pageable
        .sort
        .stream()
        .map(Order::getProperty)
        .allMatch(ALLOWED_ORDERED_PROPERTIES::contains)

    /**
     * Gets a list of all roles.
     * @return a string list of all roles.
     */
    @GetMapping("/authorities")
    suspend fun getAuthorities(): List<String> = userService
        .getAuthorities()
        .toCollection(mutableListOf())
}
