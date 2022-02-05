package backend.services.exceptions

import backend.config.Constants
import org.apache.commons.lang3.StringUtils

class InvalidPasswordException :
    RuntimeException("Incorrect password") {
    companion object {
        private const val serialVersionUID = 1L
    }
    fun isPasswordLengthInvalid(password: String?): Boolean =
        if (StringUtils.isEmpty(password)) false
        else (password?.length!! < Constants.PASSWORD_MIN_LENGTH) ||
                (password.length > Constants.PASSWORD_MAX_LENGTH)
}