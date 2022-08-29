package backend

import backend.services.MailService
import org.springframework.stereotype.Service

@Service
class AccountModelService(
    val accountModelRepository: IAccountModelRepository,
    val mailService: MailService
) {
    suspend fun signup(model: AccountCredentialsModel) {
        accountModelRepository.save(model)
    }
}
