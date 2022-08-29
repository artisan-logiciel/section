package backend

import backend.services.MailService
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class AccountModelService(
    private val accountModelRepository: IAccountModelRepository,
    private val mailService: MailService
) {
    suspend fun signup(model: AccountCredentialsModel) {
        //TODO: reprendre register
        InvalidPasswordException().run { if (isPasswordLengthInvalid(model.password)) throw this }

        accountModelRepository.save(model)
    }
}

object RandomUtils {
    private const val DEF_COUNT = 20
    private val SECURE_RANDOM: SecureRandom by lazy {
        SecureRandom().apply { nextBytes(ByteArray(size = 64)) }
    }

    private val generateRandomAlphanumericString: String
        get() = RandomStringUtils.random(
            DEF_COUNT,
            0,
            0,
            true,
            true,
            null,
            SECURE_RANDOM
        )

    val generatePassword: String
        get() = generateRandomAlphanumericString

    val generateActivationKey: String
        get() = generateRandomAlphanumericString

    val generateResetKey: String
        get() = generateRandomAlphanumericString
}
