package backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver
import org.springframework.data.web.ReactiveSortHandlerMethodArgumentResolver
import org.springframework.format.FormatterRegistry
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer
//import reactor.core.publisher.Hooks.onOperatorDebug
import backend.config.Log.log
import backend.properties.ApplicationProperties


@EnableWebFlux
@Configuration
class WebConfiguration(
    private val properties: ApplicationProperties
) : WebFluxConfigurer {

    override fun addFormatters(registry: FormatterRegistry) {
        DateTimeFormatterRegistrar().apply {
            setUseIsoFormat(true)
            registerFormatters(registry)
        }
    }

    @Bean
    fun corsFilter(): CorsWebFilter = CorsWebFilter(UrlBasedCorsConfigurationSource().apply source@{
        properties.cors.apply config@{
            if (
                allowedOrigins != null &&
                allowedOrigins!!.isNotEmpty()
            ) {
                log.debug("Registering CORS filter").run {
                    this@source.apply {
                        registerCorsConfiguration("/api/**", this@config)
                        registerCorsConfiguration("/management/**", this@config)
                        registerCorsConfiguration("/v2/api-docs", this@config)
                    }
                }
            }
        }
})

    // TODO: remove when this is supported in spring-data / spring-boot
    @Bean
    fun reactivePageableHandlerMethodArgumentResolver() = ReactivePageableHandlerMethodArgumentResolver()

    // TODO: remove when this is supported in spring-boot
    @Bean
    fun reactiveSortHandlerMethodArgumentResolver() = ReactiveSortHandlerMethodArgumentResolver()


    /*
        @Bean
        ResourceHandlerRegistrationCustomizer registrationCustomizer() {
            // Disable built-in cache control to use our custom filter instead
            return registration -> registration.setCacheControl(null);
        }

        @Bean
        @Profile(JHipsterConstants.SPRING_PROFILE_PRODUCTION)
        public CachingHttpHeadersFilter cachingHttpHeadersFilter() {
            // Use a cache filter that only match selected paths
            return new CachingHttpHeadersFilter(TimeUnit.DAYS.toMillis(jHipsterProperties.getHttp().getCache().getTimeToLiveInDays()));
        }
    */

}


