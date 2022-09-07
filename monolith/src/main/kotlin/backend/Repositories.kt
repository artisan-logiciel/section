@file:Suppress("unused", "FunctionN ame", "FunctionName")

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

    suspend fun save(model: AccountCredentialsModel): AccountModel?

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
        private val accounts by lazy { mutableSetOf<IAccountEntity<AuthorityRecord>>() }
    }

    override suspend fun findOneByLogin(login: String) =
        accounts.find { login.equals(it.login, ignoreCase = true) }?.toModel()

    override suspend fun findOneByEmail(email: String) =
        accounts.find { email.equals(it.email, ignoreCase = true) }?.toModel()


    override suspend fun save(model: AccountCredentialsModel): AccountModel? =
        create(model).run {
            when {
                this != null -> return@run this
                else -> update(model)
            }
        }

    private fun create(model: AccountCredentialsModel) =
        if (`mail & login do not exist`(model))
            model.copy(id = UUID.randomUUID()).apply {
                @Suppress("UNCHECKED_CAST")
                accounts += AccountEntity(this) as IAccountEntity<AuthorityRecord>
            }.toAccount() else null

    private fun `mail & login do not exist`(model: AccountCredentialsModel) =
        accounts.none {
            it.login.equals(model.login, ignoreCase = true)
                    && it.email.equals(model.email, ignoreCase = true)
        }


    private fun `mail exists and login exists`(model: AccountCredentialsModel) =
        accounts.any {
            model.email.equals(it.email, ignoreCase = true)
                    && model.login.equals(it.login, ignoreCase = true)
        }


    private fun `mail exists and login does not`(model: AccountCredentialsModel) =
        accounts.any {
            model.email.equals(it.email, ignoreCase = true)
                    && !model.login.equals(it.login, ignoreCase = true)
        }


    private fun `mail does not exist and login exists`(model: AccountCredentialsModel) =
        accounts.any {
            !model.email.equals(it.email, ignoreCase = true)
                    && model.login.equals(it.login, ignoreCase = true)
        }

    private fun update(
        model: AccountCredentialsModel,
    ): AccountModel? = when {
        `mail exists and login does not`(model) -> changeLogin(model)?.run { patch(this) }
        `mail does not exist and login exists`(model) -> changeEmail(model)?.run { patch(this) }
        `mail exists and login exists`(model) -> patch(model)
        else -> null
    }

    private fun changeLogin(
        model: AccountCredentialsModel,
    ): AccountCredentialsModel? =
        try {
            @Suppress("CAST_NEVER_SUCCEEDS")
            (accounts.first { model.email.equals(it.email, ignoreCase = true) } as AccountCredentialsModel).run {
                val retrieved: AccountCredentialsModel = copy(login = model.login)
                accounts.remove(this as IAccountEntity<AuthorityRecord>?)
                (retrieved as IAccountEntity<AuthorityRecord>?)?.run { accounts.add(this) }
                model
            }
        } catch (_: NoSuchElementException) {
            null
        }


    private fun changeEmail(
        model: AccountCredentialsModel,
    ): AccountCredentialsModel? = try {
        @Suppress("CAST_NEVER_SUCCEEDS")
        (accounts.first { model.login.equals(it.login, ignoreCase = true) } as AccountCredentialsModel).run {
            val retrieved: AccountCredentialsModel = copy(email = model.email)
            accounts.remove(this as IAccountEntity<AuthorityRecord>?)
            (retrieved as IAccountEntity<AuthorityRecord>?)?.run { accounts.add(this) }
            model
        }
    } catch (_: NoSuchElementException) {
        null
    }

    private fun patch(
        model: AccountCredentialsModel?,
    ): AccountModel? =
        model.run {
            val retrieved = accounts.find { this?.email?.equals(it.email, ignoreCase = true)!! }
            accounts.remove(accounts.find { this?.email?.equals(it.email, ignoreCase = true)!! })
            ((retrieved?.toCredentialsModel())?.copy(
                password = `if password is null or empty then no change`(model, retrieved.toCredentialsModel()),
                activationKey = `switch activationKey case then patch`(model, retrieved.toCredentialsModel()),
                authorities = `if authorities are null or empty then no change`(model, retrieved.toCredentialsModel())
            ).apply {
                @Suppress("UNCHECKED_CAST")
                accounts.add(this?.let { AccountEntity(it) } as IAccountEntity<AuthorityRecord>)
            }?.toAccount())
        }


    private fun `if password is null or empty then no change`(
        model: AccountCredentialsModel?,
        retrieved: AccountCredentialsModel
    ): String = when {
        model == null -> retrieved.password!!
        model.password == null -> retrieved.password!!
        model.password.isNotEmpty() -> model.password
        else -> retrieved.password!!
    }

    @Suppress("FunctionName")
    private fun `switch activationKey case then patch`(
        model: AccountCredentialsModel?,
        retrieved: AccountCredentialsModel
    ): String? = when {
        model == null -> null
        model.activationKey == null -> null
        !retrieved.activated
                && retrieved.activationKey.isNullOrBlank()
                && model.activationKey.isNotEmpty() -> model.activationKey

        !retrieved.activated
                && !retrieved.activationKey.isNullOrBlank()
                && model.activationKey.isNotEmpty() -> retrieved.activationKey

        else -> null
    }

    private fun `if authorities are null or empty then no change`(
        model: AccountCredentialsModel?,
        retrieved: AccountCredentialsModel
    ): Set<String> {
        if (model != null) {
            if (model.authorities != null) {
                if (model.authorities.isNotEmpty() && model.authorities.contains(ROLE_USER))
                    return model.authorities
                if (retrieved.authorities != null)
                    if (retrieved.authorities.isNotEmpty()
                        && retrieved.authorities.contains(ROLE_USER)
                        && !model.authorities.contains(ROLE_USER)
                    ) return retrieved.authorities
            } else
                if (!retrieved.authorities.isNullOrEmpty()
                    && retrieved.authorities.contains(ROLE_USER)
                ) return retrieved.authorities
        }
        return emptySet()
    }


    override suspend fun delete(account: AccountModel) {
        accounts.apply { if (isNotEmpty()) remove(find { it.id == account.id }) }
    }

    override suspend fun findActivationKeyByLogin(login: String): String? =
        accounts.find {
            it.login.equals(login, ignoreCase = true)
        }?.activationKey

    override suspend fun count(): Long = accounts.size.toLong()
    override suspend fun suppress(account: AccountModel) {
        accountAuthorityRepository.deleteAllByAccountId(account.id!!)
        delete(account)
    }

    override suspend fun signup(model: AccountCredentialsModel) {
        accountAuthorityRepository.save(save(model)?.id!!, ROLE_USER)
    }

    override suspend fun findOneActivationKey(key: String): AccountCredentialsModel? {
        TODO("Not yet implemented")
    }
}

interface IAccountAuthorityRepository {
    suspend fun save(id: UUID, authority: String): Unit

    suspend fun delete(id: UUID, authority: String): Unit

    suspend fun count(): Long

    suspend fun deleteAll(): Unit

    suspend fun deleteAllByAccountId(id: UUID): Unit
}

@Repository
class AccountAuthorityRepositoryInMemory : IAccountAuthorityRepository {
    companion object {
        private val accountAuthorities by lazy { mutableSetOf<AccountAuthority>() }
    }

    override suspend fun count(): Long = accountAuthorities.size.toLong()

    override suspend fun save(id: UUID, authority: String) {
        accountAuthorities.add(AccountAuthority(userId = id, role = authority))
    }


    override suspend fun delete(id: UUID, authority: String): Unit =
        accountAuthorities.run {
            filter { it.userId == id && it.role == authority }
                .map { remove(it) }
        }


    override suspend fun deleteAll() = accountAuthorities.clear()


    override suspend fun deleteAllByAccountId(id: UUID): Unit =
        accountAuthorities.run {
            filter { it.userId == id }
                .map { remove(it) }
        }

}
