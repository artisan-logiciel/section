@file:Suppress("unused")

package backend

import backend.services.MailService
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Service
import java.security.SecureRandom
import javax.validation.Valid
import kotlin.jvm.Throws

@Service
class AccountModelService(
    private val accountRepository: IAccountModelRepository,
    private val mailService: MailService
) {

    @Throws(
        InvalidPasswordException::class,
        UsernameAlreadyUsedException::class,
        UsernameAlreadyUsedException::class
    )
    suspend fun signup(model: AccountCredentialsModel) {
        InvalidPasswordException().run { if (isPasswordLengthInvalid(model.password)) throw this }
        checkLoginAvailable(model)
        checkEmailAvailable(model)
        accountRepository.signup(model)
        //TODO: refactor mailService pour prendre des AccountModel
//        mailService.sendActivationEmail(model.toAccount())
    }

    @Throws(UsernameAlreadyUsedException::class)
    private suspend fun checkLoginAvailable(model: AccountCredentialsModel) {
        accountRepository.findOneByLogin(model.login!!).run {
            if (this != null) when {
                !activated -> accountRepository.suppress(this)
                else -> throw UsernameAlreadyUsedException()
            }
        }
    }

    @Throws(UsernameAlreadyUsedException::class)
    private suspend fun checkEmailAvailable(model: AccountCredentialsModel) {
        accountRepository.findOneByEmail(model.email!!).run {
            if (this != null) {
                when {
                    !activated -> accountRepository.suppress(this)
                    else -> throw EmailAlreadyUsedException()
                }
            }
        }
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
