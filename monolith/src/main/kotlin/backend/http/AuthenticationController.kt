package backend.http

/**
 * Controller to authenticate users.
 */

//@RestController
//@RequestMapping("/api")
//@Suppress("unused")
//class AuthenticationController(
//    private val tokenProvider: TokenProvider,
//    private val authenticationManager: ReactiveAuthenticationManager
//) {
//    /**
//     * Object to return as body in Jwt Authentication.
//     */
//    class JwtToken(@JsonProperty(AUTHORIZATION_ID_TOKEN) val idToken: String)
//
//    @PostMapping("/authenticate")
//    suspend fun authorize(@Valid @RequestBody loginVm: Login)
//            : ResponseEntity<JwtToken> = tokenProvider.createToken(
//        authenticationManager.authenticate(
//            UsernamePasswordAuthenticationToken(
//                loginVm.username,
//                loginVm.password
//            )
//        ).awaitSingle(), loginVm.rememberMe!!
//    ).run {
//        return ResponseEntity<JwtToken>(
//            JwtToken(idToken = this),
//            HttpHeaders().apply {
//                add(
//                    AUTHORIZATION_HEADER,
//                    "$BEARER_START_WITH$this"
//                )
//            },
//            OK
//        )
//    }
//}