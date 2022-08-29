@file:Suppress("unused")

package backend

import backend.RepositoryInMemory.accountAuthorities
import backend.RepositoryInMemory.accounts
import org.springframework.stereotype.Repository
import java.util.*

object RepositoryInMemory {
    val accounts by lazy { mutableSetOf<AccountCredentialsModel>() }
    val authorities by lazy {
        mutableSetOf(
            "ADMIN",
            "USER",
            "ANONYMOUS"
        ).map { AuthorityEntity(it) }.toSet()
    }
    val accountAuthorities by lazy { mutableSetOf<UserAuthority>() }
}

interface IAccountModelRepository {
    suspend fun findOneByLogin(login: String): AccountModel?

    suspend fun findOneByEmail(email: String): AccountModel?

    suspend fun save(accountModel: AccountCredentialsModel): AccountModel?

    suspend fun delete(account: AccountModel)

    suspend fun findActivationKeyByLogin(login: String): String?

    suspend fun count(): Long
}

@Repository("accountModelRepository")
class AccountRepositoryInMemory : IAccountModelRepository {
    override suspend fun findOneByLogin(login: String): AccountModel? =
        accounts.find { it.login?.lowercase().equals(login.lowercase()) }?.toAccount()

    override suspend fun findOneByEmail(email: String): AccountModel? =
        accounts.find { it.email?.lowercase().equals(email.lowercase()) }?.toAccount()

    override suspend fun save(accountModel: AccountCredentialsModel): AccountModel? =
        accountModel.copy(id = UUID.randomUUID()).apply {
            accounts.add(accountModel)
        }.toAccount()

    override suspend fun delete(account: AccountModel) {
//        accountAuthorities.apply { filter { it.userId == account.id }.map { remove(it) } }
        accounts.apply { if (isNotEmpty()) remove(find { it.id == account.id }) }
    }

    override suspend fun findActivationKeyByLogin(login: String): String? =
        accounts.find {
            it.login?.lowercase().equals(login.lowercase())
        }?.activationKey

    override suspend fun count(): Long = accounts.size.toLong()
}

interface IAccountAuthorityRepository {
    suspend fun save(accountAuthority: UserAuthority): UserAuthority?

    suspend fun delete(accountAuthority: UserAuthority)

    suspend fun count(): Long

    suspend fun deleteAllByAccountId(id:UUID)
}

class AccountAuthorityRepositoryInMemory : IAccountAuthorityRepository {
    override suspend fun count(): Long {
        TODO("Not yet implemented")
    }

    override suspend fun save(accountAuthority: UserAuthority): UserAuthority? {
        TODO("Not yet implemented")
    }

    override suspend fun delete(accountAuthority: UserAuthority) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllByAccountId(id: UUID) {
        TODO("Not yet implemented")
    }
}