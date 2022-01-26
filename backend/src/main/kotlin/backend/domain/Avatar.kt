package backend.domain

import backend.repositories.entities.User
import java.util.*

/**
 * représente le user view model minimaliste pour la view
 */
data class Avatar(
    var id: UUID? = null,
    var login: String? = null
) {
    constructor(user: User) : this() {
        id = user.id
        login = user.login
    }
}