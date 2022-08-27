package webflux

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.Instant
import java.util.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@SpringBootApplication
class WebfluxMonolithApplication

fun main(args: Array<String>) {
    runApplication<WebfluxMonolithApplication>(*args)
}

object Constants {
    // Regex for acceptable logins
    const val LOGIN_REGEX =
        "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$"
    const val PASSWORD_MIN_LENGTH: Int = 4
    const val PASSWORD_MAX_LENGTH: Int = 100
}

/**
 * Représente le user view model
 */
data class Account(
    val id: UUID? = null,
    @field:NotBlank
    @field:Pattern(regexp = Constants.LOGIN_REGEX)
    @field:Size(min = 1, max = 50)
    val login: String? = null,
    @field:Size(max = 50)
    val firstName: String? = null,
    @field:Size(max = 50)
    val lastName: String? = null,
    @field:Email
    @field:Size(min = 5, max = 254)
    val email: String? = null,
    @field:Size(max = 256)
    val imageUrl: String? = "http://placehold.it/50x50",
    val activated: Boolean = false,
    @field:Size(min = 2, max = 10)
    val langKey: String? = null,
    val createdBy: String? = null,
    val createdDate: Instant? = null,
    val lastModifiedBy: String? = null,
    val lastModifiedDate: Instant? = null,
    val authorities: Set<String>? = null
) {
    @Suppress("unused")
    fun isActivated(): Boolean = activated
}
/**
 * Représente l'account view model avec le password
 */
data class AccountCredentials(
    @field:NotNull
    @field:Size(
        min = Constants.PASSWORD_MIN_LENGTH,
        max = Constants.PASSWORD_MAX_LENGTH
    )
    val password: String? = null,
    val activationKey: String? = null,
    val id: UUID? = null,
    @field:NotBlank
    @field:Pattern(regexp = Constants.LOGIN_REGEX)
    @field:Size(min = 1, max = 50)
    val login: String? = null,
    @field:Size(max = 50)
    val firstName: String? = null,
    @field:Size(max = 50)
    val lastName: String? = null,
    @field:Email
    @field:Size(min = 5, max = 254)
    val email: String? = null,
    @field:Size(max = 256)
    val imageUrl: String? = "http://placehold.it/50x50",
    val activated: Boolean = false,
    @field:Size(min = 2, max = 10)
    val langKey: String? = null,
    val createdBy: String? = null,
    val createdDate: Instant? = null,
    val lastModifiedBy: String? = null,
    val lastModifiedDate: Instant? = null,
    val authorities: Set<String>? = null
)
/**
 * représente le user view model minimaliste pour la view
 */
data class Avatar(
    var id: UUID? = null,
    var login: String? = null
)
data class KeyAndPassword(
    val key: String? = null,
    val newPassword: String? = null
)

data class Login(
    @field:NotNull
    val username:
    @Size(min = 1, max = 50)
    String? = null,
    @field:NotNull
    @field:Size(min = 4, max = 100)
    val password:
    String? = null,
    val rememberMe: Boolean? = null
)

data class PasswordChange(
    val currentPassword: String? = null,
    val newPassword: String? = null
)