package backend.http.problems

import org.zalando.problem.Exceptional
import backend.config.Constants.LOGIN_ALREADY_USED_TYPE

class LoginAlreadyUsedBadRequestException :
    BadRequestAlertException(
        LOGIN_ALREADY_USED_TYPE,
        "Login name already used!",
        "userManagement",
        "userexists"
    ) {
    override fun getCause(): Exceptional? = super.cause

    companion object {
        private const val serialVersionUID = 1L
    }
}