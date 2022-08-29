@file:Suppress("unused")

package backend.repositories.entities


import backend.Account
import backend.config.Constants.LOGIN_REGEX
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.*
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.validation.constraints.Email as EmailConstraint


//@Table("`phone`")
//data class Phone(
//    @Id var id: UUID? = null,
//    @field:NotNull
//    @field:Pattern(regexp = LOGIN_REGEX)
//    @field:Size(min = 1, max = 50)
//    var value: String? = null
//)

//@Table("`country_phone_code`")
//data class CountryPhoneCode(
//    @Id val code: String,
//    val countryCode: String
//) : Persistable<String> {
//    override fun getId() = code
//    override fun isNew() = true
//}

@Table("`email`")
data class Email(
    @Id val value: @EmailConstraint String
) : Persistable<String> {
    override fun getId() = value
    override fun isNew() = true
}

@Table("`authority`")
data class Authority(
    @Id
    @field:NotNull
    @field:Size(max = 50)
    val role: String
) : Persistable<String> {
    override fun getId() = role
    override fun isNew() = true
}

@Table("`user_authority`")
data class UserAuthority(
    @Id var id: Long? = null,
    @field:NotNull
    val userId: UUID,
    @field:NotNull
    val role: String
)

@Table("`user`")
data class User(
    @Id var id: UUID? = null,
    @field:NotNull
    @field:Pattern(regexp = LOGIN_REGEX)
    @field:Size(min = 1, max = 50)
    var login: String? = null,
    @JsonIgnore @Column("password_hash")
    @field:NotNull
    @field:Size(min = 60, max = 60)
    var password: String? = null,
    @field:Size(max = 50)
    var firstName: String? = null,
    @field:Size(max = 50)
    var lastName: String? = null,
    @field:EmailConstraint
    @field:Size(min = 5, max = 254)
    var email: String? = null,
    @field:NotNull
    var activated: Boolean = false,
    @field:Size(min = 2, max = 10)
    var langKey: String? = null,
    @field:Size(max = 256)
    var imageUrl: String? = null,
    @JsonIgnore
    @field:Size(max = 20)
    var activationKey: String? = null,
    @JsonIgnore
    @field:Size(max = 20)
    var resetKey: String? = null,
    var resetDate: Instant? = null,
    @JsonIgnore @Transient
    var authorities: MutableSet<Authority>? = mutableSetOf(),
    @JsonIgnore
    var createdBy: String? = null,
    @JsonIgnore @CreatedDate
    var createdDate: Instant? = Instant.now(),
    @JsonIgnore
    var lastModifiedBy: String? = null,
    @JsonIgnore @LastModifiedDate
    var lastModifiedDate: Instant? = Instant.now(),
    @Version @JsonIgnore var version: Long? = null
) {
    @PersistenceCreator
    constructor(
        id: UUID?,
        login: String?,
        password: String?,
        firstName: String?,
        lastName: String?,
        email: String?,
        activated: Boolean,
        langKey: String?,
        imageUrl: String?,
        activationKey: String?,
        resetKey: String?,
        resetDate: Instant?,
        createdBy: String?,
        createdDate: Instant?,
        lastModifiedBy: String?,
        lastModifiedDate: Instant?
    ) : this(
        id,
        login,
        password,
        firstName,
        lastName,
        email,
        activated,
        langKey,
        imageUrl,
        activationKey,
        resetKey,
        resetDate,
        mutableSetOf(),
        createdBy,
        createdDate,
        lastModifiedBy,
        lastModifiedDate
    )


    constructor(account: Account.AccountCredentials) : this() {
        User().apply {
            this@User.id = account.id
            this@User.login = account.login
            this@User.email = account.email
            this@User.firstName = account.firstName
            this@User.lastName = account.lastName
            this@User.langKey = account.langKey
            this@User.activated = account.activated
            this@User.createdBy = account.createdBy
            this@User.createdDate = account.createdDate
            this@User.lastModifiedBy = account.lastModifiedBy
            this@User.lastModifiedDate = account.lastModifiedDate
            this@User.imageUrl = account.imageUrl
            this@User.authorities = account.authorities?.map { Authority(it) }?.toMutableSet()
            this@User.password = account.password
        }
    }

    fun toAccount(): Account = Account(
        id = id,
        login = login,
        firstName = firstName,
        lastName = lastName,
        email = email,
        imageUrl = imageUrl,
        activated = activated,
        langKey = langKey,
        createdBy = createdBy,
        createdDate = createdDate,
        lastModifiedBy = lastModifiedBy,
        lastModifiedDate = lastModifiedDate,
        authorities = authorities?.map { it.role }?.toMutableSet()
    )

    fun copyAuthorities(that: User): User = this.apply {
        if (authorities == null) authorities = mutableSetOf()
        else authorities!!.clear()
        that.authorities?.forEach {
            authorities!!.add(it)
        }
    }
}



