package backend.services

import backend.Server.Log.log
import backend.domain.Account
import backend.domain.Account.AccountCredentials
import backend.repositories.AccountRepository
import backend.services.RandomUtils.generateActivationKey
import backend.services.exceptions.EmailAlreadyUsedException
import backend.services.exceptions.InvalidPasswordException
import backend.services.exceptions.UsernameAlreadyUsedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service(value = "accountService")
@Suppress("unused")
class AccountService(
    private val accountRepository: AccountRepository,
    private val mailService: MailService
) {

    @Transactional
    suspend fun register(
        accountCredentials: AccountCredentials
    ): Account = accountCredentials.apply {
        log.info("service.register user: ${this.login}, ${this.email}, ${this.firstName}, ${this.lastName}")
        log.info("test is password valid")
        InvalidPasswordException().run { if (isPasswordLengthInvalid(password)) throw this }
        accountRepository.findOneByLogin(accountCredentials.login!!).apply byLogin@{
            if (!activated) return@byLogin accountRepository.delete(account = this)
            else throw UsernameAlreadyUsedException()
        }
        accountRepository.findOneByEmail(accountCredentials.email!!).apply byEmail@{
            if (!activated) return@byEmail accountRepository.delete(account = this)
            else throw EmailAlreadyUsedException()
        }
        return accountRepository.apply {
            log.info("accountCredentials: $accountCredentials")
        }.save(
            accountCredentials.apply{
                activationKey = generateActivationKey
//                password=encryptPassword(password)
    }
        )
            /*.also {
            if (it.login == null) return@also
            if (accountRepository
                    .findActivationKeyByLogin(login = it.login!!)
                    .isNotEmpty()
            ) mailService.sendActivationEmail(it)
        }*/
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