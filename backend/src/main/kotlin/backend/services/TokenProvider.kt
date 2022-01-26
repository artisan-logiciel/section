package backend.services

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Jwts.parserBuilder
import io.jsonwebtoken.SignatureAlgorithm.HS512
import io.jsonwebtoken.io.Decoders.BASE64
import io.jsonwebtoken.jackson.io.JacksonSerializer
import io.jsonwebtoken.lang.Strings.hasLength
import io.jsonwebtoken.security.Keys.hmacShaKeyFor
import org.springframework.beans.factory.InitializingBean
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import backend.config.Constants.AUTHORITIES_KEY
import backend.config.Constants.INVALID_TOKEN
import backend.config.Constants.VALID_TOKEN
import backend.config.Log.log
import backend.properties.ApplicationProperties
import java.security.Key
import java.util.*
import kotlin.text.Charsets.UTF_8
import org.springframework.security.core.userdetails.User as UserSecurity


@Component
class TokenProvider(
    private val properties: ApplicationProperties
) : InitializingBean {

//    companion object {
        private var key: Key? = null
        private var tokenValidityInMilliseconds: Long = 0
        private var tokenValidityInMillisecondsForRememberMe: Long = 0
//    }

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        properties
            .security
            .authentication
            .jwt
            .secret!!
            .apply {
                key = hmacShaKeyFor(
                    if (!hasLength(this))
                        log.warn(
                            "Warning: the JWT key used is not Base64-encoded. " +
                                    "We recommend using the `backend.security.authentication.jwt.base64-secret`" +
                                    " key for optimum security."
                        ).run { toByteArray(UTF_8) }
                    else log.debug("Using a Base64-encoded JWT secret key").run {
                        BASE64.decode(
                            properties
                                .security
                                .authentication
                                .jwt
                                .base64Secret
                        )
                    }
                )
            }
        tokenValidityInMilliseconds = properties
            .security
            .authentication
            .jwt
            .tokenValidityInSeconds * 1000
        tokenValidityInMillisecondsForRememberMe = properties
            .security
            .authentication
            .jwt
            .tokenValidityInSecondsForRememberMe * 1000
    }

    suspend fun createToken(
        authentication: Authentication,
        rememberMe: Boolean
    ): String {
        Date().time.apply {
            return@createToken Jwts.builder()
                .setSubject(authentication.name)
                .claim(
                    AUTHORITIES_KEY,
                    authentication.authorities
                        .asSequence()
                        .map { it.authority }
                        .joinToString(separator = ","))
                .signWith(key, HS512)
                .setExpiration(
                    if (rememberMe) Date(this + tokenValidityInMillisecondsForRememberMe)
                    else Date(this + tokenValidityInMilliseconds)
                )
                .serializeToJsonWith(JacksonSerializer())
                .compact()
        }
    }

    fun getAuthentication(token: String): Authentication {
        parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
            .apply {
                this[AUTHORITIES_KEY]
                    .toString()
                    .splitToSequence(",")
                    .mapTo(mutableListOf()) { SimpleGrantedAuthority(it) }
                    .apply authorities@{
                        return@getAuthentication UsernamePasswordAuthenticationToken(
                            UserSecurity(subject, "", this@authorities),
                            token,
                            this@authorities
                        )
                    }
            }
    }

    fun validateToken(token: String): Boolean {
        try {
            parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            return VALID_TOKEN
        } catch (e: JwtException) {
            log.info("Invalid JWT token.")
            log.trace("Invalid JWT token trace. $e")
        } catch (e: IllegalArgumentException) {
            log.info("Invalid JWT token.")
            log.trace("Invalid JWT token trace. $e")
        }
        return INVALID_TOKEN
    }
}


