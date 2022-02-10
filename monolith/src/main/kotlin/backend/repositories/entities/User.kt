package backend.repositories.entities

//import common.config.Constants.LOGIN_REGEX
//import com.fasterxml.jackson.annotation.JsonIgnore
//import org.springframework.data.annotation.*
//import org.springframework.data.relational.core.mapping.Column
//import org.springframework.data.relational.core.mapping.Table
//import java.time.Instant
//import java.util.*
//import javax.validation.constraints.NotNull
//import javax.validation.constraints.Pattern
//import javax.validation.constraints.Size
//import javax.validation.constraints.Email as EmailConstraint

//@Table("`user`")
//data class User(
//    @Id var id: UUID? = null,
//    @field:NotNull
//    @field:Pattern(regexp = LOGIN_REGEX)
//    @field:Size(min = 1, max = 50)
//    var login: String? = null,
//    @JsonIgnore @Column("password_hash")
//    @field:NotNull
//    @field:Size(min = 60, max = 60)
//    var password: String? = null,
//    @field:Size(max = 50)
//    var firstName: String? = null,
//    @field:Size(max = 50)
//    var lastName: String? = null,
//    @field:EmailConstraint
//    @field:Size(min = 5, max = 254)
//    var email: String? = null,
//    @field:NotNull
//    var activated: Boolean = false,
//    @field:Size(min = 2, max = 10)
//    var langKey: String? = null,
//    @field:Size(max = 256)
//    var imageUrl: String? = null,
//    @JsonIgnore
//    @field:Size(max = 20)
//    var activationKey: String? = null,
//    @JsonIgnore
//    @field:Size(max = 20)
//    var resetKey: String? = null,
//    var resetDate: Instant? = null,
//    @JsonIgnore @Transient
//    var authorities: MutableSet<Authority>? = mutableSetOf(),
//    @JsonIgnore
//    var createdBy: String? = null,
//    @JsonIgnore @CreatedDate
//    var createdDate: Instant? = Instant.now(),
//    @JsonIgnore
//    var lastModifiedBy: String? = null,
//    @JsonIgnore @LastModifiedDate
//    var lastModifiedDate: Instant? = Instant.now(),
//    @Version @JsonIgnore var version: Long? = null
//) {
//    @PersistenceConstructor
//    constructor(
//        id: UUID?,
//        login: String?,
//        password: String?,
//        firstName: String?,
//        lastName: String?,
//        email: String?,
//        activated: Boolean,
//        langKey: String?,
//        imageUrl: String?,
//        activationKey: String?,
//        resetKey: String?,
//        resetDate: Instant?,
//        createdBy: String?,
//        createdDate: Instant?,
//        lastModifiedBy: String?,
//        lastModifiedDate: Instant?
//    ) : this(
//        id,
//        login,
//        password,
//        firstName,
//        lastName,
//        email,
//        activated,
//        langKey,
//        imageUrl,
//        activationKey,
//        resetKey,
//        resetDate,
//        mutableSetOf(),
//        createdBy,
//        createdDate,
//        lastModifiedBy,
//        lastModifiedDate
//    )
//
//    fun copyAuthorities(that: User): User = this.apply {
//        if (authorities == null) authorities = mutableSetOf()
//        else authorities!!.clear()
//        that.authorities?.forEach {
//            authorities!!.add(it)
//        }
//    }
//}
