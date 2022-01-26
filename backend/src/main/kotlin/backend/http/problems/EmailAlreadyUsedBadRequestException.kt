package backend.http.problems

import backend.config.Constants.EMAIL_ALREADY_USED_TYPE
//TODO: renomer EmailAlreadyUsedBadRequestException en EmailAlreadyUsedBadRequestException,
// créer la meme classe dans service problems
// nommage :
// AlreadyUsedServiceException      service
// AlreadyUsedBadRequestException   http
class EmailAlreadyUsedBadRequestException :
    BadRequestAlertException(
        EMAIL_ALREADY_USED_TYPE,
        "Email is already in use!",
        "userManagement",
        "emailexists"
    ) {
    companion object {
        private const val serialVersionUID = 1L
    }
}