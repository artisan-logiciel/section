//package backend.http.problems
//
//import org.zalando.problem.Exceptional
//import backend.config.Constants.LOGIN_ALREADY_USED_TYPE
//
//class LoginAlreadyUsedProblem :
//    AlertProblem(
//        type = LOGIN_ALREADY_USED_TYPE,
//        defaultMessage = "Login name already used!",
//        entityName = "userManagement",
//        errorKey = "userexists"
//    ) {
//    override fun getCause(): Exceptional? = super.cause
//
//    companion object {
//        private const val serialVersionUID = 1L
//    }
//}