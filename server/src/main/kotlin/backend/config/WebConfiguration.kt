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
import backend.Server.Log.log
import backend.config.Constants.SPRING_PROFILE_PRODUCTION
import backend.http.filters.JwtFilter
import backend.http.filters.SpaWebFilter
import backend.properties.ApplicationProperties
import backend.services.TokenProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.apache.commons.mail.EmailConstants
import org.apache.commons.mail.EmailConstants.*
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.server.WebExceptionHandler
import org.zalando.problem.jackson.ProblemModule
import org.zalando.problem.spring.webflux.advice.ProblemExceptionHandler
import org.zalando.problem.spring.webflux.advice.ProblemHandling
import org.zalando.problem.spring.webflux.advice.security.SecurityProblemSupport
import org.zalando.problem.violations.ConstraintViolationProblemModule
import reactor.core.publisher.Hooks

@Configuration
@EnableWebFlux
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Import(SecurityProblemSupport::class)
@Suppress("unused")
class WebConfiguration(
    private val properties: ApplicationProperties,
    private val userDetailsService: ReactiveUserDetailsService,
    private val tokenProvider: TokenProvider,
    private val problemSupport: SecurityProblemSupport,
) : WebFluxConfigurer {

    override fun addFormatters(registry: FormatterRegistry) {
        DateTimeFormatterRegistrar().apply {
            setUseIsoFormat(true)
            registerFormatters(registry)
        }
    }

    @Bean
    fun validator(): Validator = LocalValidatorFactoryBean()

    @Bean
    fun javaTimeModule(): JavaTimeModule = JavaTimeModule()

    @Bean
    fun jdk8TimeModule(): Jdk8Module = Jdk8Module()

    @Bean
    fun javaMailSender(): JavaMailSender = JavaMailSenderImpl()
        .apply {
            host = properties.mail.host
            port = properties.mail.port
            username = properties.mail.from
            password = properties.mail.password
            javaMailProperties.apply {
                this[MAIL_TRANSPORT_PROTOCOL] = properties.mail.property.transport.protocol
                this[MAIL_SMTP_AUTH] = properties.mail.property.smtp.auth
                this[MAIL_TRANSPORT_STARTTLS_ENABLE] = properties.mail.property.smtp.starttls.enable
                this[MAIL_DEBUG] = properties.mail.property.debug
            }
        }


    /**
     * The handler must have precedence over
     * WebFluxResponseStatusExceptionHandler
     * and Spring Boot's ErrorWebExceptionHandler
     */
    @Bean
    @Order(-2)
    fun problemHandler(
        mapper: ObjectMapper,
        problemHandling: ProblemHandling
    ): WebExceptionHandler = ProblemExceptionHandler(mapper, problemHandling)

    @Bean
    fun problemModule(): ProblemModule = ProblemModule()

    @Bean
    fun constraintViolationProblemModule() = ConstraintViolationProblemModule()

    @Profile("!$SPRING_PROFILE_PRODUCTION")
    fun reactorConfiguration() = Hooks.onOperatorDebug()

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
    @Bean("passwordEncoder")
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun reactiveAuthenticationManager(): ReactiveAuthenticationManager =
        UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService)
            .apply { setPasswordEncoder(passwordEncoder()) }

    @Bean
    fun springSecurityFilterChain(
        http: ServerHttpSecurity
    ): SecurityWebFilterChain =
        @Suppress("DEPRECATION")
        http.securityMatcher(
            NegatedServerWebExchangeMatcher(
                OrServerWebExchangeMatcher(
                    ServerWebExchangeMatchers.pathMatchers(
                        "/app/**",
                        "/i18n/**",
                        "/content/**",
                        "/swagger-ui/**",
                        "/test/**",
                        "/webjars/**"
                    ),
                    ServerWebExchangeMatchers.pathMatchers(HttpMethod.OPTIONS, "/**")
                )
            )
        ).csrf()
            .disable()
            .addFilterAt(SpaWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .addFilterAt(JwtFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
            .authenticationManager(reactiveAuthenticationManager())
            .exceptionHandling()
            .accessDeniedHandler(problemSupport)
            .authenticationEntryPoint(problemSupport)
            .and()
            .headers().contentSecurityPolicy(Constants.CONTENT_SECURITY_POLICY)
            .and()
            .referrerPolicy(ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            .and()
            .featurePolicy(Constants.FEATURE_POLICY)
            .and()
            .frameOptions().disable()
            .and()
            .authorizeExchange()
            .pathMatchers("/").permitAll()
            .pathMatchers("/**").permitAll()
            .pathMatchers("/*.*").permitAll()
            .pathMatchers("/api/register").permitAll()
            .pathMatchers("/api/activate").permitAll()
            .pathMatchers("/api/authenticate").permitAll()
            .pathMatchers("/api/account/reset-password/init").permitAll()
            .pathMatchers("/api/account/reset-password/finish").permitAll()
            .pathMatchers("/api/auth-info").permitAll()
            .pathMatchers("/api/user/**").permitAll()
            .pathMatchers("/management/health").permitAll()
            .pathMatchers("/management/health/**").permitAll()
            .pathMatchers("/management/info").permitAll()
            .pathMatchers("/management/prometheus").permitAll()
            .pathMatchers("/api/**").permitAll()
            .pathMatchers("/services/**").authenticated()
            .pathMatchers("/swagger-resources/**").authenticated()
            .pathMatchers("/v2/api-docs").authenticated()
            .pathMatchers("/management/**").hasAuthority(Constants.ROLE_ADMIN)
            .pathMatchers("/api/admin/**").hasAuthority(Constants.ROLE_ADMIN)
            .and()
            .build()
}


