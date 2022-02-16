package backend.services

import backend.repositories.AccountRepository
import backend.services.exceptions.InvalidPasswordException
import common.domain.Account
import common.domain.Account.AccountCredentials
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
        InvalidPasswordException().run { if (isPasswordLengthInvalid(password)) throw this }

//        accountRepository.findOneByLogin(accountCredentials.login!!).apply byLogin@{
//            if (!activated) return@byLogin accountRepository.delete(account = this)
//            else throw backend.services.exceptions.UsernameAlreadyUsedException()
//        }
//
//        accountRepository.findOneByEmail(accountCredentials.email!!).apply byEmail@{
//            if (!activated) return@byEmail accountRepository.delete(account = this)
//            else throw EmailAlreadyUsedException()
//        }
//
//        return accountRepository.save(
//            AccountCredentials(
//                password = password,
//                activationKey = backend.services.RandomUtils.generateActivationKey
//            )
//        ).also {
//            if (accountRepository.findActivationKeyByLogin(login = it.login!!).isNotEmpty())
//                mailService.sendActivationEmail(it)
//        }
    }
}




