package reaktive.services

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder.getContext
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.userdetails.UserDetails
import reaktive.config.Constants.ROLE_ANONYMOUS

@Suppress("ClassName")
object SecurityUtils {

    suspend fun getCurrentUserLogin(): String =
        extractPrincipal(
            getContext()
                .awaitSingle()
                .authentication
        )

    fun extractPrincipal(authentication: Authentication?): String =
        if (authentication == null) ""
        else when (val principal = authentication.principal) {
            is UserDetails -> principal.username
            is String -> principal
            else -> ""
        }

    suspend fun getCurrentUserJWT(): String =
        getContext()
            .map(SecurityContext::getAuthentication)
            .filter { it.credentials is String }
            .map { it.credentials as String }
            .awaitSingle()

    suspend fun isAuthenticated(): Boolean =
        getContext()
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getAuthorities)
            .map { roles ->
                roles.map(transform = GrantedAuthority::getAuthority)
                    .none { it == ROLE_ANONYMOUS }
            }.awaitSingle()


    suspend fun isCurrentUserInRole(authority: String): Boolean =
        getContext()
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getAuthorities)
            .map { roles ->
                roles.map(transform = GrantedAuthority::getAuthority)
                    .any { it == authority }
            }.awaitSingle()
}