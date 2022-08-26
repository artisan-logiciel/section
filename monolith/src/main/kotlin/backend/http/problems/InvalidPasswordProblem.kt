//package backend.http.problems
//
//import backend.config.Constants.INVALID_PASSWORD_TYPE
//import org.zalando.problem.AbstractThrowableProblem
//import org.zalando.problem.Exceptional
//import org.zalando.problem.Status.BAD_REQUEST
//
//class InvalidPasswordProblem : AbstractThrowableProblem(
//    INVALID_PASSWORD_TYPE,
//    "Incorrect password",
//    BAD_REQUEST
//) {
//    override fun getCause(): Exceptional? = super.cause
//
//    companion object {
//        private const val serialVersionUID = 1L
//    }
//}