package backend.repositories.entities

import backend.config.Constants.LOGIN_REGEX
import backend.domain.Account
import backend.domain.UserData
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.annotation.Transient
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.validation.constraints.Email as EmailConstraint

@Table("`user`")
data class User(
    @Id override var id: UUID? = null,
    @field:NotNull
    @field:Pattern(regexp = LOGIN_REGEX)
    @field:Size(min = 1, max = 50)
    override var login: String? = null,
    @JsonIgnore @Column("password_hash")
    @field:NotNull
    @field:Size(min = 60, max = 60)
    override var password: String? = null,
    @field:Size(max = 50)
    override var firstName: String? = null,
    @field:Size(max = 50)
    override var lastName: String? = null,
    @field:EmailConstraint
    @field:Size(min = 5, max = 254)
    override var email: String? = null,
    @field:NotNull
    override var activated: Boolean = false,
    @field:Size(min = 2, max = 10)
    override var langKey: String? = null,
    @field:Size(max = 256)
    override var imageUrl: String? = null,
    @JsonIgnore
    @field:Size(max = 20)
    override var activationKey: String? = null,
    @JsonIgnore
    @field:Size(max = 20)
    override var resetKey: String? = null,
    var resetDate: Instant? = null,
    @JsonIgnore @Transient
    override var authorities: MutableSet<Authority>? = mutableSetOf(),
    @Version @JsonIgnore override var version: Long? = null
) : AbstractAuditingEntity(), UserData {
    @PersistenceConstructor
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
    )

    fun copyAuthorities(that: User): User = this.apply {
        if (authorities == null) authorities = mutableSetOf()
        else authorities!!.clear()
        that.authorities?.forEach {
            authorities!!.add(it)
        }
    }

    fun toAccount(): Account = Account().apply acc@{
        this@acc.activated = this@User.activated
        this@acc.createdBy = this@User.createdBy
        this@acc.createdDate = this@User.createdDate
        this@acc.lastModifiedBy = this@User.lastModifiedBy
        this@acc.lastModifiedDate = this@User.lastModifiedDate
        this@acc.id = this@User.id
        this@acc.email = this@User.email
        this@acc.login = this@User.login
        this@acc.firstName = this@User.firstName
        this@acc.lastName = this@User.lastName
        this@acc.langKey = this@User.langKey
        this@acc.imageUrl = this@User.imageUrl
        mutableSetOf<String>().apply {
            this@acc.authorities = this
            this@User.authorities?.forEach {
                (this@acc.authorities!! as MutableSet).add(it.role)
            }
        }
    }
}
