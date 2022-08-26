//package backend.http.problems
//
//import backend.config.Constants.EMAIL_ALREADY_USED_TYPE
//
//class EmailAlreadyUsedProblem :
//    AlertProblem(
//        type = EMAIL_ALREADY_USED_TYPE,
//        defaultMessage = "Email is already in use!",
//        entityName = "userManagement",
//        errorKey = "emailexists"
//    ) {
//    companion object {
//        private const val serialVersionUID = 1L
//    }
//}