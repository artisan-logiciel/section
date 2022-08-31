package backend

//@Suppress("ClassName")
//object SecurityUtils {
//
//    suspend fun getCurrentUserLogin(): String =
//        extractPrincipal(
//            getContext()
//                .awaitSingle()
//                .authentication
//        )
//
//    private fun extractPrincipal(authentication: Authentication?): String =
//        if (authentication == null) ""
//        else when (val principal = authentication.principal) {
//            is UserDetails -> principal.username
//            is String -> principal
//            else -> ""
//        }
//
//    suspend fun getCurrentUserJwt(): String =
//        getContext()
//            .map(SecurityContext::getAuthentication)
//            .filter { it.credentials is String }
//            .map { it.credentials as String }
//            .awaitSingle()
//
//    suspend fun isAuthenticated(): Boolean =
//        getContext()
//            .map(SecurityContext::getAuthentication)
//            .map(Authentication::getAuthorities)
//            .map { roles: Collection<GrantedAuthority> ->
//                roles.map(transform = GrantedAuthority::getAuthority)
//                    .none { it == ROLE_ANONYMOUS }
//            }.awaitSingle()
//
//
//    suspend fun isCurrentUserInRole(authority: String): Boolean =
//        getContext()
//            .map(SecurityContext::getAuthentication)
//            .map(Authentication::getAuthorities)
//            .map { roles: Collection<GrantedAuthority> ->
//                roles.map(transform = GrantedAuthority::getAuthority)
//                    .any { it == authority }
//            }.awaitSingle()
//}