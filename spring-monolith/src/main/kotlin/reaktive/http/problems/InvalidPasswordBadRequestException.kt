package reaktive.http.problems

import org.zalando.problem.AbstractThrowableProblem
import org.zalando.problem.Exceptional
import org.zalando.problem.Status.BAD_REQUEST
import reaktive.config.Constants.INVALID_PASSWORD_TYPE

class InvalidPasswordBadRequestException : AbstractThrowableProblem(
    INVALID_PASSWORD_TYPE,
    "Incorrect password",
    BAD_REQUEST
) {
    override fun getCause(): Exceptional? = super.cause

    companion object {
        private const val serialVersionUID = 1L
    }
}