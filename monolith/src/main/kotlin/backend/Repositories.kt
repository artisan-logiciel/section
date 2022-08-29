@file:Suppress("unused")

package backend


import org.springframework.stereotype.Repository
import java.util.*

interface IAuthorityRepository {
    suspend fun findOne(role: String): AuthorityEntity?
}

@Repository
class AuthorityRepositoryInMemory : IAuthorityRepository {
    companion object {
        val authorities by lazy {
            mutableSetOf(
                "ADMIN",
                "USER",
                "ANONYMOUS"
            ).map { AuthorityEntity(it) }.toSet()
        }
    }

    override suspend fun findOne(role: String): AuthorityEntity? =
        authorities.find { it.role == role }

}

interface IAccountModelRepository {
    suspend fun findOneByLogin(login: String): AccountModel?

    suspend fun findOneByEmail(email: String): AccountModel?

    suspend fun save(accountModel: AccountCredentialsModel): AccountModel?

    suspend fun delete(account: AccountModel)

    suspend fun findActivationKeyByLogin(login: String): String?

    suspend fun count(): Long
    suspend fun suppress(account: AccountModel)
}

@Repository
class AccountRepositoryInMemory(
    private val accountAuthorityRepository: IAccountAuthorityRepository,
    private val authorityRepository: IAuthorityRepository
) : IAccountModelRepository {

    companion object {
        val accounts by lazy { mutableSetOf<AccountCredentialsModel>() }
    }

    override suspend fun findOneByLogin(login: String): AccountModel? =
        accounts.find { it.login?.lowercase().equals(login.lowercase()) }?.toAccount()

    override suspend fun findOneByEmail(email: String): AccountModel? =
        accounts.find { it.email?.lowercase().equals(email.lowercase()) }?.toAccount()

    override suspend fun save(accountModel: AccountCredentialsModel): AccountModel? =
        accountModel.copy(id = UUID.randomUUID()).apply {
            accounts.add(accountModel)
        }.toAccount()

    override suspend fun delete(account: AccountModel) {
        accounts.apply { if (isNotEmpty()) remove(find { it.id == account.id }) }
    }

    override suspend fun findActivationKeyByLogin(login: String): String? =
        accounts.find {
            it.login?.lowercase().equals(login.lowercase())
        }?.activationKey

    override suspend fun count(): Long = accounts.size.toLong()
    override suspend fun suppress(account: AccountModel) {
        TODO("Not yet implemented")
    }
}

interface IAccountAuthorityRepository {
    suspend fun save(accountAuthority: UserAuthority): UserAuthority

    suspend fun delete(accountAuthority: UserAuthority)

    suspend fun count(): Long

    suspend fun deleteAll()

    suspend fun deleteAllByAccountId(id: UUID)
}

@Repository
class AccountAuthorityRepositoryInMemory : IAccountAuthorityRepository {
    companion object {
        val accountAuthorities by lazy { mutableSetOf<UserAuthority>() }
    }

    override suspend fun count(): Long = accountAuthorities.size.toLong()

    override suspend fun save(accountAuthority: UserAuthority): UserAuthority =
        accountAuthority.apply { accountAuthorities.add(this) }


    override suspend fun delete(accountAuthority: UserAuthority) {
        when {
            accountAuthorities.contains(accountAuthority) -> accountAuthorities.remove(accountAuthority)
        }
    }

    override suspend fun deleteAll() = accountAuthorities.clear()


    override suspend fun deleteAllByAccountId(id: UUID) {
        accountAuthorities.apply {
            filter { it.userId == id }
                .map { remove(it) }
        }
    }
}