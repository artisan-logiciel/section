@file:Suppress("unused")

package backend.http.filters


import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import backend.config.Constants.AUTHORIZATION_HEADER
import backend.config.Constants.BEARER_START_WITH
import backend.services.TokenProvider

@Component("jwtFilter")
class JWTFilter(private val tokenProvider: TokenProvider) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        resolveToken(exchange.request).apply token@{
            chain.apply {
                return if (!isNullOrBlank() &&
                    tokenProvider.validateToken(token = this@token)
                ) filter(exchange)
                    .contextWrite(
                        withAuthentication(
                            tokenProvider.getAuthentication(token = this@token)
                        )
                    )
                else filter(exchange)
            }
        }
    }

    private fun resolveToken(request: ServerHttpRequest): String? = request
        .headers
        .getFirst(AUTHORIZATION_HEADER)
        .apply {
            return if (
                !isNullOrBlank() &&
                startsWith(BEARER_START_WITH)
            ) substring(startIndex = 7)
            else null
        }

}