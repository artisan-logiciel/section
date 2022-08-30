@file:Suppress("unused")

package backend


import backend.Constants.ROLE_USER
import org.springframework.stereotype.Repository
import java.util.*

interface IAuthorityRepository {
    suspend fun findOne(role: String): String?
}

@Repository
class AuthorityRepositoryInMemory : IAuthorityRepository {
    companion object {
        private val authorities by lazy {
            mutableSetOf(
                "ADMIN",
                "USER",
                "ANONYMOUS"
            ).map { AuthorityEntity(it) }.toSet()
        }
    }

    override suspend fun findOne(role: String): String? =
        authorities.find { it.role == role }?.role

}

interface IAccountModelRepository {
    suspend fun findOneByLogin(login: String): AccountModel?

    suspend fun findOneByEmail(email: String): AccountModel?

    suspend fun save(accountCredentialsModel: AccountCredentialsModel): AccountModel?

    suspend fun delete(account: AccountModel)

    suspend fun findActivationKeyByLogin(login: String): String?

    suspend fun count(): Long
    suspend fun suppress(account: AccountModel)
    suspend fun signup(model: AccountCredentialsModel)
    suspend fun findOneActivationKey(key: String): AccountCredentialsModel?
}

@Repository
class AccountRepositoryInMemory(
    private val accountAuthorityRepository: IAccountAuthorityRepository,
    private val authorityRepository: IAuthorityRepository
) : IAccountModelRepository {

    companion object {
        val accounts by lazy { mutableSetOf<AccountEntity>() }
    }

    override suspend fun findOneByLogin(login: String): AccountModel? =
        accounts.find { it.login?.lowercase().equals(login.lowercase()) }?.toModel()

    override suspend fun findOneByEmail(email: String): AccountModel? =
        accounts.find { it.email?.lowercase().equals(email.lowercase()) }?.toModel()

    override suspend fun save(accountCredentialsModel: AccountCredentialsModel): AccountModel? =
        when {
            accountCredentialsModel.id == null
                    && accounts.none { it.login?.lowercase() == accountCredentialsModel.login }
                    && accounts.none { it.email?.lowercase() == accountCredentialsModel.email }
            -> accountCredentialsModel.copy(id = UUID.randomUUID())
                .apply { accounts.add(AccountEntity(this)) }
                .toAccount()

            accountCredentialsModel.id != null
                    && accounts.none { it.login?.lowercase() == accountCredentialsModel.login }
                    && accounts.none { it.email?.lowercase() == accountCredentialsModel.email }
            -> AccountEntity(
                accountCredentialsModel
            ).apply {
                try {
                    accounts.remove(accounts.first { this.id == it.id })
                    accounts.add(this)
                } catch (_: NoSuchElementException) {
                }
            }.toModel()

            else -> accountCredentialsModel.toAccount()
        }


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

    override suspend fun signup(model: AccountCredentialsModel) {
        accountAuthorityRepository.save(save(model)?.id!!, ROLE_USER)
        save(model)
    }

    override suspend fun findOneActivationKey(key: String): AccountCredentialsModel? {
        TODO("Not yet implemented")
    }
}

interface IAccountAuthorityRepository {
    suspend fun save(id: UUID, authority: String)

    suspend fun delete(id: UUID, authority: String)

    suspend fun count(): Long

    suspend fun deleteAll()

    suspend fun deleteAllByAccountId(id: UUID)
}

@Repository
class AccountAuthorityRepositoryInMemory : IAccountAuthorityRepository {
    companion object {
        private val accountAuthorities by lazy { mutableSetOf<UserAuthority>() }
    }

    override suspend fun count(): Long = accountAuthorities.size.toLong()

    override suspend fun save(id: UUID, authority: String) {
        accountAuthorities.add(UserAuthority(userId = id, role = authority))
    }


    override suspend fun delete(id: UUID, authority: String) {
        accountAuthorities.apply {
            filter { it.userId == id && it.role == authority }
                .map { remove(it) }
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