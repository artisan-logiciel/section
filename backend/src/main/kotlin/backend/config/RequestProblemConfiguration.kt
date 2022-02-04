package backend.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.web.server.WebExceptionHandler
import org.zalando.problem.jackson.ProblemModule
import org.zalando.problem.spring.webflux.advice.ProblemExceptionHandler
import org.zalando.problem.spring.webflux.advice.ProblemHandling
import org.zalando.problem.violations.ConstraintViolationProblemModule

@Suppress("unused")
@Configuration
class RequestProblemConfiguration {

    @Bean
    @Order(-2) // The handler must have precedence over WebFluxResponseStatusExceptionHandler
    // and Spring Boot's ErrorWebExceptionHandler
    fun problemHandler(
        mapper: ObjectMapper,
        problemHandling: ProblemHandling
    ): WebExceptionHandler = ProblemExceptionHandler(mapper, problemHandling)

    @Bean
    fun problemModule(): ProblemModule = ProblemModule()

    @Bean
    fun constraintViolationProblemModule() = ConstraintViolationProblemModule()
}