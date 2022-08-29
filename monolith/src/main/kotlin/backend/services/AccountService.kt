@file:Suppress("unused")

package backend.services

import backend.domain.Account
import backend.domain.Account.AccountCredentials
import backend.repositories.AccountRepository
import backend.services.RandomUtils.generateActivationKey
import backend.services.exceptions.InvalidPasswordException
import backend.services.exceptions.UsernameAlreadyUsedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

//import org.springframework.security.crypto.password.PasswordEncoder
@Service(value = "accountService")
class AccountService(
    private val accountRepository: AccountRepository,
    private val mailService: MailService,
//    private val passwordEncoder:PasswordEncoder
) {
    @Transactional
    suspend fun register(
        accountCredentials: AccountCredentials
    ) {
        InvalidPasswordException().run { if (isPasswordLengthInvalid(accountCredentials.password)) throw this }

        accountRepository.findOneByLogin(accountCredentials.login!!)?.run {
            when {
                !activated -> accountRepository.delete(account = this)
                else -> throw UsernameAlreadyUsedException()
            }
        }
//        log.info(accountRepository.findOneByEmail(accountCredentials.email!!))
//            .run {
//            if (id != null && !activated) accountRepository.delete(account = this)
//            else throw EmailAlreadyUsedException()
//        }

        accountRepository.save(
            accountCredentials.copy(
                //                password = password,//encrypt
                activationKey = generateActivationKey
            )
        ).also {
            when {
                accountRepository
                    .findActivationKeyByLogin(login = accountCredentials.login!!)
                    .isNotEmpty() -> mailService.sendActivationEmail(it)
            }
        }
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