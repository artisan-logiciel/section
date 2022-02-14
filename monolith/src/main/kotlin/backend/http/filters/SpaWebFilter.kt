//package backend.http.filters
//
//
//import org.springframework.stereotype.Component
//import org.springframework.web.server.ServerWebExchange
//import org.springframework.web.server.WebFilter
//import org.springframework.web.server.WebFilterChain
//import reactor.core.publisher.Mono
//
//
//@Component
//@Suppress("unused")
//class SpaWebFilter : WebFilter {
//    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
//        exchange.request.uri.path.apply {
//            return if (
//                !this.startsWith("/api") &&
//                !this.startsWith("/management") &&
//                !this.startsWith("/services") &&
//                !this.startsWith("/swagger") &&
//                !this.startsWith("/v2/api-docs") &&
//                this.matches(Regex("[^\\\\.]*"))
//            ) chain.filter(
//                exchange.mutate().request(
//                    exchange.request
//                        .mutate()
//                        .path("/index.html")
//                        .build()
//                ).build()
//            ) else chain.filter(exchange)
//        }
//    }
//}