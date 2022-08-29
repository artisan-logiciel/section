@file:Suppress("unused")

package backend.repositories

import backend.Log.log
import backend.User
import backend.config.Constants.SYSTEM_USER
import backend.Account
import backend.Account.AccountCredentials
import org.springframework.stereotype.Repository

@Repository(value = "accountRepository")
class AccountRepositoryR2dbc(
    private val userRepository: UserRepository
) : AccountRepository {

    override suspend fun findOneByLogin(login: String) =
        userRepository.findOneWithAuthoritiesByLogin(login)?.toAccount()

    override suspend fun findOneByEmail(email: String) =
        userRepository.findOneByEmail(email)?.toAccount()
/*
    override suspend fun findOneByEmail(email: String): Account =
        userRepository.findOneByEmail(email).apply {
            if (this == null) return Account()
        }?.toAccount()!!

 */
    override suspend fun findActivationKeyByLogin(login: String): String =
        userRepository.findOneByLogin(login)?.activationKey.toString()

    override suspend fun delete(account: Account) = account.run {
        if (login == null) return@run
        else {
            userRepository.findOneByLogin(login!!)?.apply u@{
                userRepository.delete(user = this@u)
            }.run { log.debug("Changed Information for User: ${this?.login}") }
        }
    }


    override suspend fun save(accountCredentials: AccountCredentials): Account {
        return (accountCredentials.login).run currentUserLogin@{
            accountCredentials.apply {
                SYSTEM_USER.apply systemUser@{
                    if (createdBy.isNullOrBlank()) {
                        createdBy = this@systemUser
                        lastModifiedBy = this@systemUser
                    } else lastModifiedBy = this@currentUserLogin
                }
                return@currentUserLogin userRepository.save(User(account = this)).toAccount()
            }
        }
    }
//    @Transactional
//    suspend fun saveUser(user: User): User = SecurityUtils.getCurrentUserLogin()
//        .run currentUserLogin@{
//            user.apply user@{
//                SYSTEM_USER.apply systemUser@{
//                    if (createdBy.isNullOrBlank()) {
//                        createdBy = this@systemUser
//                        lastModifiedBy = this@systemUser
//                    } else lastModifiedBy = this@currentUserLogin
//                }
//                userRepository.save(this@user)
//            }
//        }
}