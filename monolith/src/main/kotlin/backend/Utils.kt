@file:Suppress("unused")

package backend

import backend.Log.log
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.http.HttpHeaders
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.SecureRandom

object RandomUtils {
    private const val DEF_COUNT = 20
    private val SECURE_RANDOM: SecureRandom by lazy {
        SecureRandom().apply { nextBytes(ByteArray(size = 64)) }
    }

    private val generateRandomAlphanumericString: String
        get() = RandomStringUtils.random(
            DEF_COUNT, 0, 0, true, true, null, SECURE_RANDOM
        )

    val generatePassword: String
        get() = generateRandomAlphanumericString

    val generateActivationKey: String
        get() = generateRandomAlphanumericString

    val generateResetKey: String
        get() = generateRandomAlphanumericString
}

//@Suppress("ClassName")
//object SecurityUtils {
//
//    suspend fun getCurrentUserLogin(): String =
//        extractPrincipal(
//            getContext()
//                .awaitSingle()
//                .authentication
//        )
//
//    private fun extractPrincipal(authentication: Authentication?): String =
//        if (authentication == null) ""
//        else when (val principal = authentication.principal) {
//            is UserDetails -> principal.username
//            is String -> principal
//            else -> ""
//        }
//
//    suspend fun getCurrentUserJwt(): String =
//        getContext()
//            .map(SecurityContext::getAuthentication)
//            .filter { it.credentials is String }
//            .map { it.credentials as String }
//            .awaitSingle()
//
//    suspend fun isAuthenticated(): Boolean =
//        getContext()
//            .map(SecurityContext::getAuthentication)
//            .map(Authentication::getAuthorities)
//            .map { roles: Collection<GrantedAuthority> ->
//                roles.map(transform = GrantedAuthority::getAuthority)
//                    .none { it == ROLE_ANONYMOUS }
//            }.awaitSingle()
//
//
//    suspend fun isCurrentUserInRole(authority: String): Boolean =
//        getContext()
//            .map(SecurityContext::getAuthentication)
//            .map(Authentication::getAuthorities)
//            .map { roles: Collection<GrantedAuthority> ->
//                roles.map(transform = GrantedAuthority::getAuthority)
//                    .any { it == authority }
//            }.awaitSingle()
//}


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