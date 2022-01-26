package reaktive.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod.OPTIONS
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION
import org.springframework.security.config.web.server.SecurityWebFiltersOrder.HTTP_BASIC
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers
import org.zalando.problem.spring.webflux.advice.security.SecurityProblemSupport
import reaktive.config.Constants.CONTENT_SECURITY_POLICY
import reaktive.config.Constants.FEATURE_POLICY
import reaktive.config.Constants.ROLE_ADMIN
import reaktive.http.filters.JWTFilter
import reaktive.http.filters.SpaWebFilter
import reaktive.services.TokenProvider

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Import(SecurityProblemSupport::class)
class SecurityConfiguration(
    private val userDetailsService: ReactiveUserDetailsService,
    private val tokenProvider: TokenProvider,
    private val problemSupport: SecurityProblemSupport,
) {
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
                    pathMatchers(
                        "/app/**",
                        "/i18n/**",
                        "/content/**",
                        "/swagger-ui/**",
                        "/test/**",
                        "/webjars/**"
                    ),
                    pathMatchers(OPTIONS, "/**")
                )
            )
        ).csrf()
            .disable()
            .addFilterAt(SpaWebFilter(), AUTHENTICATION)
            .addFilterAt(JWTFilter(tokenProvider), HTTP_BASIC)
            .authenticationManager(reactiveAuthenticationManager())
            .exceptionHandling()
            .accessDeniedHandler(problemSupport)
            .authenticationEntryPoint(problemSupport)
            .and()
            .headers().contentSecurityPolicy(CONTENT_SECURITY_POLICY)
            .and()
            .referrerPolicy(STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            .and()
            .featurePolicy(FEATURE_POLICY)
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
            .pathMatchers("/management/**").hasAuthority(ROLE_ADMIN)
            .pathMatchers("/api/admin/**").hasAuthority(ROLE_ADMIN)
            .and()
            .build()
}