@file:Suppress("unused")

package backend

import backend.services.MailService
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Service
import java.security.SecureRandom
import javax.validation.Valid

@Service
class AccountModelService(
    private val accountRepository: IAccountModelRepository,
    private val mailService: MailService
) {

    suspend fun signup(model: AccountCredentialsModel) {
        InvalidPasswordException().run { if (isPasswordLengthInvalid(model.password)) throw this }
        accountRepository.findOneByLogin(model.login!!).run {
            if (this != null) when {
                !activated -> accountRepository.suppress(this)
                else -> throw UsernameAlreadyUsedException()
            }
        }
        accountRepository.findOneByEmail(model.email!!).run {
            if (this != null) {
                when {
                    !activated -> accountRepository.suppress(this)
                    else -> throw EmailAlreadyUsedException()
                }
            }
        }
        accountRepository.signup(model)
//            .also {
//            //TODO: mail
//            when {
//                accountRepository
//                    .findActivationKeyByLogin(login = accountCredentials.login!!)
//                    .isNotEmpty() -> mailService.sendActivationEmail(it)
//            }
//        }
    }

    private suspend fun suppress(model: AccountCredentialsModel) {
        accountRepository.suppress(model.toAccount())
    }
}

object RandomUtils {
    private const val DEF_COUNT = 20
    private val SECURE_RANDOM: SecureRandom by lazy {
        SecureRandom().apply { nextBytes(ByteArray(size = 64)) }
    }

    private val generateRandomAlphanumericString: String
        get() = RandomStringUtils.random(
            DEF_COUNT, 0, 0, true, true, null, SECURE_RANDOM
        )

    val generatePassword: String
        get() = generateRandomAlphanumericString

    val generateActivationKey: String
        get() = generateRandomAlphanumericString

    val generateResetKey: String
        get() = generateRandomAlphanumericString
}
