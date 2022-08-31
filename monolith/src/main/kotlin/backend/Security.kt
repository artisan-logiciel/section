package backend

//import backend.UserRepository
//import backend.repositories.entities.User
//@Component("userDetailsService")
//@Suppress("unused")
//class DomainUserDetailsService(
//    private val userRepository: UserRepository
//) : ReactiveUserDetailsService {
//
//    @Transactional
//    override fun findByUsername(login: String): Mono<UserDetails> = log
//        .debug("Authenticating $login").run {
//            return if (EmailValidator().isValid(login, null)) mono {
//                userRepository.findOneWithAuthoritiesByEmail(login).apply {
//                    if (this == null) throw UsernameNotFoundException(
//                        "User with email $login was not found in the database"
//                    )
//                }
//            }.map { createSpringSecurityUser(login, it) }
//            else mono {
//                userRepository.findOneWithAuthoritiesByLogin(login).apply {
//                    if (this == null) throw UsernameNotFoundException(
//                        "User $login was not found in the database"
//                    )
//                }
//            }.map { createSpringSecurityUser(login, it) }
//        }
//
//
//    private fun createSpringSecurityUser(
//        lowercaseLogin: String,
//        user: User
//    ): UserSecurity = if (!user.activated) {
//        throw UserNotActivatedException(
//            "User $lowercaseLogin was not activated"
//        )
//    } else UserSecurity(
//        user.login!!,
//        user.password!!,
//        user.authorities!!.map {
//            SimpleGrantedAuthority(it.role)
//        }
//    )
//}
//@Component
//class TokenProvider(
//    private val properties: ApplicationProperties
//) : InitializingBean {
//
//    private var key: Key? = null
//    private var tokenValidityInMilliseconds: Long = 0
//    private var tokenValidityInMillisecondsForRememberMe: Long = 0
//
//    @Throws(Exception::class)
//    override fun afterPropertiesSet() {
//        properties
//            .security
//            .authentication
//            .jwt
//            .secret!!
//            .apply {
//                key = hmacShaKeyFor(
//                    if (!hasLength(this))
//                        log.warn(
//                            "Warning: the Jwt key used is not Base64-encoded. " +
//                                    "We recommend using the `backend.security.authentication.jwt.base64-secret`" +
//                                    " key for optimum security."
//                        ).run { toByteArray(UTF_8) }
//                    else log.debug("Using a Base64-encoded Jwt secret key").run {
//                        BASE64.decode(
//                            properties
//                                .security
//                                .authentication
//                                .jwt
//                                .base64Secret
//                        )
//                    }
//                )
//            }
//        tokenValidityInMilliseconds = properties
//            .security
//            .authentication
//            .jwt
//            .tokenValidityInSeconds * 1000
//        tokenValidityInMillisecondsForRememberMe = properties
//            .security
//            .authentication
//            .jwt
//            .tokenValidityInSecondsForRememberMe * 1000
//    }
//
//    suspend fun createToken(
//        authentication: Authentication,
//        rememberMe: Boolean
//    ): String {
//        Date().time.apply {
//            return@createToken Jwts.builder()
//                .setSubject(authentication.name)
//                .claim(
//                    AUTHORITIES_KEY,
//                    authentication.authorities
//                        .asSequence()
//                        .map { it.authority }
//                        .joinToString(separator = ","))
//                .signWith(key, HS512)
//                .setExpiration(
//                    if (rememberMe) Date(this + tokenValidityInMillisecondsForRememberMe)
//                    else Date(this + tokenValidityInMilliseconds)
//                )
//                .serializeToJsonWith(JacksonSerializer())
//                .compact()
//        }
//    }
//
//    fun getAuthentication(token: String): Authentication {
//        parserBuilder()
//            .setSigningKey(key)
//            .build()
//            .parseClaimsJws(token)
//            .body
//            .apply {
//                this[AUTHORITIES_KEY]
//                    .toString()
//                    .splitToSequence(",")
//                    .mapTo(mutableListOf()) { SimpleGrantedAuthority(it) }
//                    .apply authorities@{
//                        return@getAuthentication UsernamePasswordAuthenticationToken(
//                            UserSecurity(subject, "", this@authorities),
//                            token,
//                            this@authorities
//                        )
//                    }
//            }
//    }
//
//    fun validateToken(token: String): Boolean {
//        try {
//            parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//            return VALID_TOKEN
//        } catch (e: JwtException) {
//            log.info("Invalid Jwt token.")
//            log.trace("Invalid Jwt token trace. $e")
//        } catch (e: IllegalArgumentException) {
//            log.info("Invalid Jwt token.")
//            log.trace("Invalid Jwt token trace. $e")
//        }
//        return INVALID_TOKEN
//    }
//}
//
//
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