package backend.http

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import backend.config.Constants.AUTHORIZATION_HEADER
import backend.config.Constants.AUTHORIZATION_ID_TOKEN
import backend.config.Constants.BEARER_START_WITH
import backend.domain.Login
import backend.services.TokenProvider
import javax.validation.Valid

/**
 * Controller to authenticate users.
 */

@RestController
@RequestMapping("/api")
@Suppress("unused")
class AuthenticationController(
    private val tokenProvider: TokenProvider,
    private val authenticationManager: ReactiveAuthenticationManager
) {
    /**
     * Object to return as body in Jwt Authentication.
     */
    class JwtToken(@JsonProperty(AUTHORIZATION_ID_TOKEN) val idToken: String)

    @PostMapping("/authenticate")
    suspend fun authorize(@Valid @RequestBody loginVm: Login)
            : ResponseEntity<JwtToken> = tokenProvider.createToken(
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginVm.username,
                loginVm.password
            )
        ).awaitSingle(), loginVm.rememberMe!!
    ).run {
        return ResponseEntity<JwtToken>(
            JwtToken(idToken = this),
            HttpHeaders().apply {
                add(
                    AUTHORIZATION_HEADER,
                    "$BEARER_START_WITH$this"
                )
            },
            OK
        )
    }
}