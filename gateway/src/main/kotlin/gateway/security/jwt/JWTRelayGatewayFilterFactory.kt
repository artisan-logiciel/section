package gateway.security.jwt

import gateway.security.jwt.JWTFilter.Companion.AUTHORIZATION_HEADER
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

@Component
class JWTRelayGatewayFilterFactory(private val tokenProvider: TokenProvider) : AbstractGatewayFilterFactory<Any>() {

    override fun apply(config: Any) =
        GatewayFilter { exchange, chain ->
            if (exchange != null) {
                val token = extractJWTToken(exchange.request)
                if (token != null && StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                    val request = exchange.request.mutate()
                        .header(AUTHORIZATION_HEADER, "Bearer $token")
                        .build()
                    return@GatewayFilter chain.filter(exchange.mutate().request(request).build())
                }
            }
            chain.filter(exchange)
        }

    private fun extractJWTToken(request: ServerHttpRequest): String? {
        val bearerToken = request.headers.getFirst(AUTHORIZATION_HEADER)
        if (bearerToken == null) {
            return null
        }
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        throw IllegalArgumentException("Invalid token in Authorization header")
    }
}
