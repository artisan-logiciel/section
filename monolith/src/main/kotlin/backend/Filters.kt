package backend

//@Component("jwtFilter")
//@Suppress("unused")
//class JwtFilter(private val tokenProvider: TokenProvider) : WebFilter {
//
//    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
//        resolveToken(exchange.request).apply token@{
//            chain.apply {
//                return if (!isNullOrBlank() &&
//                    tokenProvider.validateToken(token = this@token)
//                ) filter(exchange)
//                    .contextWrite(
//                        withAuthentication(
//                            tokenProvider.getAuthentication(token = this@token)
//                        )
//                    )
//                else filter(exchange)
//            }
//        }
//    }
//
//    private fun resolveToken(request: ServerHttpRequest): String? = request
//        .headers
//        .getFirst(AUTHORIZATION_HEADER)
//        .apply {
//            return if (
//                !isNullOrBlank() &&
//                startsWith(BEARER_START_WITH)
//            ) substring(startIndex = 7)
//            else null
//        }
//
//}