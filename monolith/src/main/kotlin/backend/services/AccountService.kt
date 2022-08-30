@file:Suppress("unused")

package backend.services

import backend.*
import backend.Account.AccountCredentials
import backend.repositories.AccountRepository
import backend.RandomUtils.generateActivationKey
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

        accountCredentials.copy(
            //                password = password,//encrypt
            activationKey = generateActivationKey
        ).run {
            accountRepository.save(this)
            when {
                accountRepository
                    .findActivationKeyByLogin(login = accountCredentials.login!!)
                    .isNotEmpty() -> mailService.sendActivationEmail(
                    AccountCredentialsModel(
                        password = password,
                        activationKey = activationKey,
                        id = id,
                        login = login,
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        imageUrl = imageUrl,
                        activated = activated,
                        langKey = langKey,
                        createdBy = createdBy,
                        createdDate = createdDate,
                        lastModifiedBy = lastModifiedBy,
                        lastModifiedDate = lastModifiedDate,
                        authorities = authorities
                    )
                )
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