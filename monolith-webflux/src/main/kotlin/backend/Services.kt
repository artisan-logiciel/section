package backend

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    val userRepository: IUserRepository,
    val mailService: MailService
) {
    @Transactional
    suspend fun register(user: UserCredentialsModel) {

    }
}

@Service
class MailService

