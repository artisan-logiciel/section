@file:Suppress("unused")

package backend

import backend.domain.Account
import backend.repositories.entities.UserAuthority
import org.springframework.stereotype.Repository

object RepositoryInMemory {
    val accounts by lazy { mutableSetOf<AccountModel>() }
    val authorities by lazy {
        mutableSetOf(
            "ADMIN",
            "USER",
            "ANONYMOUS"
        ).map { AuthorityEntity(it) }.toSet()
    }
    val accountAuthorities by lazy { mutableSetOf<UserAuthority>() }
}

interface IAccountRepository {
    suspend fun findOneByLogin(login: String): AccountModel?

    suspend fun findOneByEmail(email: String): AccountModel?

    suspend fun save(accountCredentials: Account.AccountCredentials): AccountModel

    suspend fun delete(account: AccountModel)

    suspend fun findActivationKeyByLogin(login: String): String
}

@Repository("accountModelRepository")
class AccountRepositoryInMemory : IAccountRepository {
    override suspend fun findOneByLogin(login: String): AccountModel? {
        return RepositoryInMemory.accounts.find { it.login?.lowercase().equals(login) }
    }

    override suspend fun findOneByEmail(email: String): AccountModel? {
        TODO("Not yet implemented")
    }

    override suspend fun save(accountCredentials: Account.AccountCredentials): AccountModel {
        TODO("Not yet implemented")
    }

    override suspend fun delete(account: AccountModel) {
        TODO("Not yet implemented")
    }

    override suspend fun findActivationKeyByLogin(login: String): String {
        TODO("Not yet implemented")
    }

}
