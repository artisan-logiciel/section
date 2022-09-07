package backend

import backend.Constants.IMAGE_URL_DEFAULT
import java.time.Instant
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.validation.constraints.Email as EmailConstraints

/**
 * Représente le user view model sans le password
 */
//TODO: add field enabled=false
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
    val imageUrl: String? = IMAGE_URL_DEFAULT,
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
    val imageUrl: String? = IMAGE_URL_DEFAULT,
    val activated: Boolean = false,
    @field:Size(min = 2, max = 10)
    val langKey: String? = null,
    val createdBy: String? = null,
    val createdDate: Instant? = null,
    val lastModifiedBy: String? = null,
    val lastModifiedDate: Instant? = null,
    val authorities: Set<String>? = null
) {

    constructor(account: AccountModel) : this(
        id = account.id,
        login = account.login,
        email = account.email,
        firstName = account.firstName,
        lastName = account.lastName,
        langKey = account.langKey,
        activated = account.activated,
        createdBy = account.createdBy,
        createdDate = account.createdDate,
        lastModifiedBy = account.lastModifiedBy,
        lastModifiedDate = account.lastModifiedDate,
        imageUrl = account.imageUrl,
        authorities = account.authorities?.map { it }?.toMutableSet()
    )


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