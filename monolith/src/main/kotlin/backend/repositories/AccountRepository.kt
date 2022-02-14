package backend.repositories

import common.domain.Account
import org.springframework.stereotype.Service


interface AccountRepository {

    suspend fun findOneByLogin(login: String): Account?

    suspend fun findOneByEmail(email: String): Account? {
        TODO("Not yet implemented")
    }

    suspend fun save(accountCredentials: Account.AccountCredentials): Account {
        TODO("Not yet implemented")
    }

    suspend fun delete(account: Account) {
        TODO("Not yet implemented")
    }
}

@Service(value = "accountRepository")
class AccountRepositoryImpl(
    private val userRepository: UserRepository
) : AccountRepository {
    override suspend fun findOneByLogin(login: String): Account? =
        userRepository.findOneWithAuthoritiesByLogin(login).apply {
            if (this == null) return null
        }!!.toAccount()
}


//    suspend fun findAccountByEmail(email: String): Account? =
//        Account(userRepository.findOneByEmail(email).apply {
//            if (this == null) return null
//        }!!)
//
//    suspend fun findAccountByLogin(login: String): Account? =
//        Account(userRepository.findOneWithAuthoritiesByLogin(login).apply {
//            if (this == null) return null
//        }!!)
//

