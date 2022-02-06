package backend.domain

import backend.config.Constants.DEFAULT_LANGUAGE
import backend.config.Constants.LOGIN_REGEX
import backend.repositories.entities.Authority
import backend.repositories.entities.User
import java.time.Instant
import java.util.*
import java.util.stream.Collectors.toSet
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.validation.constraints.Email as EmailConstraint


/**
 * Représente le user view model
 */
open class Account(
    var id: UUID? = null,
    @field:NotBlank
    @field:Pattern(regexp = LOGIN_REGEX)
    @field:Size(min = 1, max = 50)
    var login: String? = null,
    @field:Size(max = 50)
    var firstName: String? = null,
    @field:Size(max = 50)
    var lastName: String? = null,
    @field:EmailConstraint
    @field:Size(min = 5, max = 254)
    var email: String? = null,
    @field:Size(max = 256)
    var imageUrl: String? = "http://placehold.it/50x50",
    var activated: Boolean = false,
    @field:Size(min = 2, max = 10)
    var langKey: String? = null,
    var createdBy: String? = null,
    var createdDate: Instant? = null,
    var lastModifiedBy: String? = null,
    var lastModifiedDate: Instant? = null,
    var authorities: Set<String>? = null
) {
    constructor(user: User) : this() {
        id = user.id
        login = user.login
        firstName = user.firstName
        lastName = user.lastName
        email = user.email
        activated = user.activated
        imageUrl = user.imageUrl
        langKey = user.langKey
        createdBy = user.createdBy
        createdDate = user.createdDate
        lastModifiedBy = user.lastModifiedBy
        lastModifiedDate = user.lastModifiedDate
        authorities = user.authorities!!
            .stream()
            .map(Authority::role)
            .collect(toSet())
    }

    fun toUser(): User = User(
        id = id,
        login = login,
        firstName = firstName,
        lastName = lastName,
        email = email,
        activated = activated,
        imageUrl = imageUrl,
        langKey = if (langKey.isNullOrEmpty() || langKey.isNullOrBlank())
            DEFAULT_LANGUAGE
        else langKey,
        createdBy = createdBy,
        createdDate = createdDate,
        lastModifiedBy = lastModifiedBy,
        lastModifiedDate = lastModifiedDate,
        authorities = if (authorities.isNullOrEmpty()) mutableSetOf()
        else authorities!!
            .stream()
            .map {
                Authority(role = it)
            }.collect(toSet())
    )
    @Suppress("unused")
    fun isActivated(): Boolean = activated
}


