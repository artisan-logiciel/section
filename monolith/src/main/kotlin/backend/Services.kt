package backend

import backend.Log.log
import backend.services.MailService
import org.springframework.stereotype.Service

@Service
class AccountModelService(
    val accountModelRepository: IAccountModelRepository,
    val mailService: MailService
) {
    suspend fun signup(model: AccountCredentialsModel) {
        log.info("signup for $model")
    }
}
