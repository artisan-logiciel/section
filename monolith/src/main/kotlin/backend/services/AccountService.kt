package backend.services

import backend.repositories.AccountRepository
import backend.services.RandomUtils.generateActivationKey
import backend.services.exceptions.EmailAlreadyUsedException
import backend.services.exceptions.InvalidPasswordException
import backend.services.exceptions.UsernameAlreadyUsedException
import common.domain.Account
import common.domain.Account.AccountCredentials
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Suppress("unused")
class AccountService(private val accountRepository: AccountRepository) {

//        userService.register(account.apply {
//            InvalidPasswordException().apply {
//                if (isPasswordLengthInvalid(password!!)) throw this
//            }
//        }, account.password!!)
//            ?.also {
//                if (!userService.getUserWithAuthoritiesByLogin(account.email!!)
//                        ?.activationKey
//                        .isNullOrBlank()
//                ) mailService.sendActivationEmail(it)
//            }!!


    @Transactional
    suspend fun register(
        account: AccountCredentials
    ): Account = account.apply {
        InvalidPasswordException().apply ipex@{
            if (isPasswordLengthInvalid(password)) throw this@ipex
        }
        accountRepository.findOneByLogin(login!!)?.apply isActivatedOnCheckLogin@{
            if (!activated) return@isActivatedOnCheckLogin accountRepository.delete(account = this)
            else throw UsernameAlreadyUsedException()
        }
        .also {
            accountRepository.findOneByEmail(account.email!!)
                ?.apply isActivatedOnCheckEmail@{
                    if (!activated) return@isActivatedOnCheckEmail accountRepository.delete(account = this)
                    else throw EmailAlreadyUsedException()
                }
        }
        .apply {
            return@register accountRepository.save(
                AccountCredentials(
                    password = password,
                    activationKey = generateActivationKey
                )
            )
}
            }
    }


