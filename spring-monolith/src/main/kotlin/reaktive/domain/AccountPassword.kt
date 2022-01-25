package reaktive.domain

import reaktive.config.Constants.PASSWORD_MAX_LENGTH
import reaktive.config.Constants.PASSWORD_MIN_LENGTH
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Représente l'account view model avec le password
 */
class AccountPassword(
    @field:NotNull
    @field:Size(
        min = PASSWORD_MIN_LENGTH,
        max = PASSWORD_MAX_LENGTH
    ) var password: String? = null
) : Account()