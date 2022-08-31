@file:Suppress("unused")

package backend

import backend.Constants.CONSTRAINT_VIOLATION_TYPE
import backend.Constants.DEFAULT_TYPE
import backend.Constants.ERR_CONCURRENCY_FAILURE
import backend.Constants.ERR_VALIDATION
import backend.Constants.SPRING_PROFILE_PRODUCTION
import backend.HttpHeaderUtil.createFailureAlert
import backend.Log.log
import org.springframework.core.env.Environment
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebExchange
import org.zalando.problem.*
import org.zalando.problem.Problem.builder
import org.zalando.problem.Status.BAD_REQUEST
import org.zalando.problem.spring.webflux.advice.ProblemHandling
import org.zalando.problem.violations.ConstraintViolationProblem
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.just
import java.io.Serializable
import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import org.zalando.problem.Problem.DEFAULT_TYPE as PROBLEM_DEFAULT_TYPE

/**
 * Utility class for HTTP headers creation.
 */
object HttpHeaderUtil {

    /**
     *
     * createAlert.
     *
     * @param applicationName a [java.lang.String] object.
     * @param message a [java.lang.String] object.
     * @param param a [java.lang.String] object.
     * @return a [org.springframework.http.HttpHeaders] object.
     */
    fun createAlert(
        applicationName: String,
        message: String?,
        param: String?
    ): HttpHeaders {
        val headers = HttpHeaders()
        headers.add("X-$applicationName-alert", message)
        try {
            headers.add("X-$applicationName-params", URLEncoder.encode(param, StandardCharsets.UTF_8.toString()))
        } catch (e: UnsupportedEncodingException) {
            // StandardCharsets are supported by every Java implementation so this exceptions will never happen
        }
        return headers
    }

    /**
     *
     * createEntityCreationAlert.
     *
     * @param applicationName a [java.lang.String] object.
     * @param enableTranslation a boolean.
     * @param entityName a [java.lang.String] object.
     * @param param a [java.lang.String] object.
     * @return a [org.springframework.http.HttpHeaders] object.
     */
    fun createEntityCreationAlert(
        applicationName: String,
        enableTranslation: Boolean,
        entityName: String,
        param: String
    ): HttpHeaders = createAlert(
        applicationName,
        if (enableTranslation) "$applicationName.$entityName.created"
        else "A new $entityName is created with identifier $param",
        param
    )

    /**
     *
     * createEntityUpdateAlert.
     *
     * @param applicationName a [java.lang.String] object.
     * @param enableTranslation a boolean.
     * @param entityName a [java.lang.String] object.
     * @param param a [java.lang.String] object.
     * @return a [org.springframework.http.HttpHeaders] object.
     */
    fun createEntityUpdateAlert(
        applicationName: String,
        enableTranslation: Boolean,
        entityName: String,
        param: String
    ): HttpHeaders = createAlert(
        applicationName,
        if (enableTranslation) "$applicationName.$entityName.updated"
        else "A $entityName is updated with identifier $param",
        param
    )

    /**
     *
     * createEntityDeletionAlert.
     *
     * @param applicationName a [java.lang.String] object.
     * @param enableTranslation a boolean.
     * @param entityName a [java.lang.String] object.
     * @param param a [java.lang.String] object.
     * @return a [org.springframework.http.HttpHeaders] object.
     */
    fun createEntityDeletionAlert(
        applicationName: String,
        enableTranslation: Boolean,
        entityName: String,
        param: String
    ): HttpHeaders = createAlert(
        applicationName,
        if (enableTranslation) "$applicationName.$entityName.deleted"
        else "A $entityName is deleted with identifier $param",
        param
    )

    /**
     *
     * createFailureAlert.
     *
     * @param applicationName a [java.lang.String] object.
     * @param enableTranslation a boolean.
     * @param entityName a [java.lang.String] object.
     * @param errorKey a [java.lang.String] object.
     * @param defaultMessage a [java.lang.String] object.
     * @return a [org.springframework.http.HttpHeaders] object.
     */
    fun createFailureAlert(
        applicationName: String,
        enableTranslation: Boolean,
        entityName: String?,
        errorKey: String,
        defaultMessage: String?
    ): HttpHeaders = log.error(
        "Entity processing failed, {}",
        defaultMessage
    ).run {
        return@run HttpHeaders().apply {
            add(
                "X-$applicationName-error",
                if (enableTranslation) "error.$errorKey"
                else defaultMessage!!
            )
            add(
                "X-$applicationName-params",
                entityName
            )
        }
    }
}
/**
 * Controller advice to translate the backend side problems to client-friendly json structures.
 * The error response follows RFC7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807).
 */
@Component
@ControllerAdvice
class ProblemTranslator(
    private val env: Environment,
    private val properties: ApplicationProperties
) : ProblemHandling/*, SecurityAdviceTrait*/ {

    companion object {
        private const val FIELD_ERRORS_KEY = "fieldErrors"
        private const val MESSAGE_KEY = "message"
        private const val PATH_KEY = "path"
        private const val VIOLATIONS_KEY = "violations"
    }

    class FieldErrorVM(
        @Suppress("unused")
        val objectName: String,
        val field: String,
        val message: String?
    ) : Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }
    }

    /**
     * Post-process the Problem payload to add the message key for the front-end if needed.
     */
    override fun process(
        entity: ResponseEntity<Problem>,
        request: ServerWebExchange
    ): Mono<ResponseEntity<Problem>> {
        @Suppress("SENSELESS_COMPARISON")
        if (entity == null) return Mono.empty()
        val problem = entity.body
        if (
            !(problem is ConstraintViolationProblem
                    || problem is DefaultProblem)
        ) return just(entity)
        val builder = builder()
            .withType(
                if (PROBLEM_DEFAULT_TYPE == problem.type) DEFAULT_TYPE
                else problem.type
            )
            .withStatus(problem.status)
            .withTitle(problem.title)
            .with(PATH_KEY, request.request.path.value())

        if (problem is ConstraintViolationProblem) builder
            .with(VIOLATIONS_KEY, problem.violations)
            .with(MESSAGE_KEY, ERR_VALIDATION)
        else {
            builder
                .withCause((problem as DefaultProblem).cause)
                .withDetail(problem.detail)
                .withInstance(problem.instance)
            problem.parameters.forEach { (key, value) -> builder.with(key, value) }
            if (!problem.parameters.containsKey(MESSAGE_KEY) && problem.status != null) {
                builder.with(
                    MESSAGE_KEY,
                    "error.http." + problem.status!!.statusCode
                )
            }
        }
        return just(
            ResponseEntity<Problem>(
                builder.build(),
                entity.headers,
                entity.statusCode
            )
        )
    }

    override fun handleBindingResult(
        ex: WebExchangeBindException, request: ServerWebExchange
    ): Mono<ResponseEntity<Problem>> = create(
        ex, builder()
            .withType(CONSTRAINT_VIOLATION_TYPE)
            .withTitle("Data binding and validation failure")
            .withStatus(BAD_REQUEST)
            .with(MESSAGE_KEY, ERR_VALIDATION)
            .with(FIELD_ERRORS_KEY, ex.bindingResult.fieldErrors.map {
                FieldErrorVM(
                    it.objectName.replaceFirst(
                        Regex(pattern = "DTO$"),
                        replacement = ""
                    ), it.field, it.code
                )
            }).build(), request
    )

    @ExceptionHandler
    fun handleEmailAlreadyUsedException(
        ex: EmailAlreadyUsedException,
        request: ServerWebExchange
    ): Mono<ResponseEntity<Problem>> {
        val problem = EmailAlreadyUsedProblem()
        return create(
            problem,
            request,
            createFailureAlert(
                applicationName = properties.clientApp.name,
                enableTranslation = true,
                entityName = problem.entityName,
                errorKey = problem.errorKey,
                defaultMessage = problem.message
            )
        )
    }

    @ExceptionHandler
    fun handleUsernameAlreadyUsedException(
        ex: UsernameAlreadyUsedException,
        request: ServerWebExchange
    ): Mono<ResponseEntity<Problem>> {
        val problem = LoginAlreadyUsedProblem()
        return create(
            problem,
            request,
            createFailureAlert(
                applicationName = properties.clientApp.name,
                enableTranslation = true,
                entityName = problem.entityName,
                errorKey = problem.errorKey,
                defaultMessage = problem.message
            )
        )
    }

    @ExceptionHandler
    fun handleInvalidPasswordException(
        ex: InvalidPasswordException,
        request: ServerWebExchange
    ): Mono<ResponseEntity<Problem>> = create(
        InvalidPasswordProblem(),
        request
    )

    @ExceptionHandler
    fun handleBadRequestAlertException(
        ex: AlertProblem,
        request: ServerWebExchange
    ): Mono<ResponseEntity<Problem>> =
        create(
            ex, request,
            createFailureAlert(
                applicationName = properties.clientApp.name,
                enableTranslation = true,
                entityName = ex.entityName,
                errorKey = ex.errorKey,
                defaultMessage = ex.message
            )
        )

    @ExceptionHandler
    fun handleConcurrencyFailure(
        ex: ConcurrencyFailureException,
        request: ServerWebExchange
    ): Mono<ResponseEntity<Problem>> = create(
        ex, builder()
            .withStatus(Status.CONFLICT)
            .with(MESSAGE_KEY, ERR_CONCURRENCY_FAILURE)
            .build(), request
    )

    override fun prepare(
        throwable: Throwable,
        status: StatusType,
        type: URI
    ): ProblemBuilder {
        var detail = throwable.message
        if (env.activeProfiles.contains(SPRING_PROFILE_PRODUCTION)) {
            detail = when (throwable) {
                is HttpMessageConversionException -> "Unable to convert http message"
                is DataAccessException -> "Failure during data access"
                else -> {
                    if (containsPackageName(throwable.message))
                        "Unexpected runtime exceptions"
                    else throwable.message
                }
            }
        }
        return builder()
            .withType(type)
            .withTitle(status.reasonPhrase)
            .withStatus(status)
            .withDetail(detail)
            .withCause(throwable.cause.takeIf { isCausalChainsEnabled }?.let { toProblem(it) })
    }

    private fun containsPackageName(message: String?) =
        listOf(
            "org.",
            "java.",
            "net.",
            "javax.",
            "com.",
            "io.",
            "de.",
            "backend"
        ).any { it == message }


}