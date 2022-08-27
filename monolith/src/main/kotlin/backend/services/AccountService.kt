package backend.services

import backend.domain.Account
import backend.domain.Account.AccountCredentials
import backend.repositories.AccountRepository
import backend.services.RandomUtils.generateActivationKey
import backend.services.exceptions.EmailAlreadyUsedException
import backend.services.exceptions.InvalidPasswordException
import backend.services.exceptions.UsernameAlreadyUsedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

//import org.springframework.security.crypto.password.PasswordEncoder
@Service(value = "accountService")
@Suppress("unused")
class AccountService(
    private val accountRepository: AccountRepository,
    private val mailService: MailService,
//    private val passwordEncoder:PasswordEncoder
) {
    @Transactional
    suspend fun register(accountCredentials: AccountCredentials) {

        InvalidPasswordException().apply {
            if (isPasswordLengthInvalid(accountCredentials.password)) throw this
        }


        accountRepository.findOneByLogin(accountCredentials.login!!).run {
            when {
                !activated -> accountRepository.delete(account = accountCredentials)
                else -> throw UsernameAlreadyUsedException()
            }
        }


        accountRepository.findOneByEmail(accountCredentials.email!!).run {
            when {
                !activated -> accountRepository.delete(account = accountCredentials)
                else -> throw EmailAlreadyUsedException()
            }
        }

        accountRepository.save(
            accountCredentials.apply {
                activationKey = generateActivationKey
                //                password=PasswordEncoder(password)
            }
        )

        mailService.sendActivationEmail(accountCredentials)
    }

    fun activateRegistration(key: String): Account? {
        TODO("Not yet implemented")
    }
//    @Transactional
//    suspend fun activateRegistration(key: String): User? =
//        log.debug("Activating user for activation key {}", key).run {
//            return@run iUserRepository.findOneByActivationKey(key).apply {
//                if (this != null) {
//                    activated = true
//                    activationKey = null
//                    saveUser(user = this).run {
//                        log.debug("Activated user: {}", this)
//                    }
//                } else log.debug("No user found with activation key {}", key)
//            }
//        }
}