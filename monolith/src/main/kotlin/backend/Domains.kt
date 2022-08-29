package backend

import java.time.Instant
import java.util.*
import javax.validation.constraints.*
import javax.validation.constraints.Email as EmailConstraints

/**
 * Représente le user view model sans le password
 */
open class Account(
    var id: UUID? = null,
    @field:NotBlank
    @field:Pattern(regexp = Constants.LOGIN_REGEX)
    @field:Size(min = 1, max = 50)
    open var login: String? = null,
    @field:Size(max = 50)
    open var firstName: String? = null,
    @field:Size(max = 50)
    open var lastName: String? = null,
    @field:EmailConstraints
    @field:Size(min = 5, max = 254)
    open var email: String? = null,
    @field:Size(max = 256)
    open var imageUrl: String? = "http://placehold.it/50x50",
    open var activated: Boolean = false,
    @field:Size(min = 2, max = 10)
    open var langKey: String? = null,
    var createdBy: String? = null,
    var createdDate: Instant? = null,
    var lastModifiedBy: String? = null,
    var lastModifiedDate: Instant? = null,
    open var authorities: Set<String>? = null
) {
    @Suppress("unused")
    fun isActivated(): Boolean = activated

    /**
     * Représente l'account view model avec le password
     */
    data class AccountCredentials(
        @field:NotNull
        @field:Size(
            min = Constants.PASSWORD_MIN_LENGTH,
            max = Constants.PASSWORD_MAX_LENGTH
        )
        var password: String? = null,
        var activationKey: String? = null
    ) : Account()


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
}

/**
 * Représente le user view model
 */
data class AccountDomain(
    val id: UUID? = null,
    @field:NotBlank
    @field:Pattern(regexp = Constants.LOGIN_REGEX)
    @field:Size(min = 1, max = 50)
    val login: String? = null,
    @field:Size(max = 50)
    val firstName: String? = null,
    @field:Size(max = 50)
    val lastName: String? = null,
    @field:EmailConstraints
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
data class AccountCredentialsDomain(
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
    @field:EmailConstraints
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
data class AvatarDomain(
    val id: UUID? = null,
    val login: String? = null
)
data class KeyAndPasswordDomain(
    val key: String? = null,
    val newPassword: String? = null
)

data class LoginDomain(
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

data class PasswordChangeDomain(
    val currentPassword: String? = null,
    val newPassword: String? = null
)

/**
 * Représente le user view model sans le password
 */
data class AccountModel(
    val id: UUID? = null,
    @field:NotBlank
    @field:Pattern(regexp = Constants.LOGIN_REGEX)
    @field:Size(min = 1, max = 50)
    val login: String? = null,
    @field:Size(max = 50)
    val firstName: String? = null,
    @field:Size(max = 50)
    val lastName: String? = null,
    @field:EmailConstraints
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
data class AccountCredentialsModel(
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
    @field:EmailConstraints
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
    fun toAccount(): AccountModel = AccountModel(
        id = id,
        login = login,
        firstName = firstName,
        email = email,
        activated = activated,
        langKey = langKey,
        createdBy = createdBy,
        createdDate = createdDate,
        lastModifiedBy = lastModifiedBy,
        lastModifiedDate = lastModifiedDate,
        authorities = authorities
    )
}

/**
 * représente le user view model minimaliste pour la view
 */
data class AvatarModel(
    val id: UUID? = null,
    val login: String? = null
)

data class KeyAndPasswordModel(
    val key: String? = null,
    val newPassword: String? = null
)

data class LoginModel(
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

data class PasswordChangeModel(
    val currentPassword: String? = null,
    val newPassword: String? = null
)