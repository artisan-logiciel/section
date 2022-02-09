package backend.domain

import backend.repositories.entities.Authority
import java.io.Serializable
import java.util.*

interface UserData : Serializable {
    var id: UUID?
    var login: String?
    var password: String?
//    @field:Size(max = 50)
    var firstName: String?
//    @field:Size(max = 50)
    var lastName: String?
//    @field:Email
//    @field:Size(min = 5, max = 254)
    var email: String?
//    @field:NotNull
    var activated: Boolean
//    @field:Size(min = 2, max = 10)
    var langKey: String?
//    @field:Size(max = 256)
    var imageUrl: String?
//    @JsonIgnore
//    @field:Size(max = 20)
    var activationKey: String?
//    @JsonIgnore
//    @field:Size(max = 20)
    var resetKey: String?
//    var resetDate: Instant? = null,
//    @JsonIgnore
//    @Transient
    var authorities: MutableSet<Authority>?
//    @Version
//    @JsonIgnore
    var version: Long?

}