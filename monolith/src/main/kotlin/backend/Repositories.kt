@file:Suppress("unused", "FunctionName", "RedundantUnitReturnType")

package backend


import backend.AuthorityRecord.Companion.ROLE_COLUMN
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.dao.DataAccessException
import org.springframework.data.r2dbc.core.*
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Repository
import reactor.kotlin.core.publisher.toMono
import java.util.*


interface AuthorityRepository {
    suspend fun findOne(role: String): String?
}

interface AccountRepository {
    suspend fun findOneByLogin(login: String): AccountCredentials?

    suspend fun findOneByEmail(email: String): AccountCredentials?

    suspend fun save(model: AccountCredentials): Account?

    suspend fun delete(account: Account)

    suspend fun findActivationKeyByLogin(login: String): String?

    suspend fun count(): Long
    suspend fun suppress(account: Account)
    suspend fun signup(model: AccountCredentials)
    suspend fun findOneByActivationKey(key: String): AccountCredentials?
}

interface AccountAuthorityRepository {
    suspend fun save(id: UUID, authority: String): Unit

    suspend fun delete(id: UUID, authority: String): Unit

    suspend fun count(): Long

    suspend fun deleteAll(): Unit

    suspend fun deleteAllByAccountId(id: UUID): Unit
}


@Repository
class AccountAuthorityRepositoryR2dbc(
    private val dao: R2dbcEntityTemplate
) : AccountAuthorityRepository {
    override suspend fun save(id: UUID, authority: String) {
        dao.insert(AccountAuthorityEntity(userId = id, role = authority))
            .awaitSingle()
    }

    override suspend fun delete(id: UUID, authority: String) {
        dao.selectOne(
            query(where("userId").`is`(id).and(where("role").`is`(authority))),
            AccountAuthorityEntity::class.java
        ).awaitSingleOrNull().run {
            if (this != null && this.id != null)
                dao.delete(this)
                    .awaitSingle()
        }
    }

    override suspend fun count(): Long = dao.select<AccountAuthorityEntity>().count()
        .awaitSingle()


    override suspend fun deleteAll() {
        dao.delete<AccountAuthorityEntity>().allAndAwait()
    }

    override suspend fun deleteAllByAccountId(id: UUID) {
        dao.delete<AccountAuthorityEntity>().matching(query(where("userId").`is`(id)))
            .allAndAwait()
    }

}


@Repository
class AccountRepositoryR2dbc(
    private val dao: R2dbcEntityTemplate
) : AccountRepository {
    override suspend fun save(model: AccountCredentials): Account? =
        try {
            dao.insert(AccountEntity(model)).awaitSingle()?.toModel()
        } catch (_: DataAccessException) {
            null
        }

    override suspend fun count(): Long = dao.select<AccountEntity>().count().awaitSingle()

    override suspend fun delete(account: Account) {
        when {
            account.login != null || account.email != null && account.id == null -> when {
                account.login != null -> findOneByLogin(account.login)
                account.email != null -> findOneByEmail(account.email)
                else -> null
            }.run { if (this != null) dao.delete(AccountEntity(this)).awaitSingle() }

            else -> dao.delete(AccountEntity(AccountCredentials(account))).awaitSingle()
        }
    }

    override suspend fun findOneByLogin(login: String): AccountCredentials? =
        dao.select<AccountEntity>().matching(query(where("login").`is`(login)))
            .awaitOneOrNull()?.toCredentialsModel()


    override suspend fun findOneByEmail(email: String): AccountCredentials? =
        dao.select<AccountEntity>().matching(query(where("email").`is`(email)))
            .awaitOneOrNull()?.toCredentialsModel()

    override suspend fun suppress(account: Account) {
        dao.run {
            delete<AccountAuthorityEntity>().matching(query(where("userId").`is`(account.id!!)))
                .allAndAwait()
            delete(AccountEntity(AccountCredentials(account))).awaitSingle()
        }
    }


    override suspend fun signup(model: AccountCredentials) {
        dao.run {
            AccountEntity(model).run {
                insert(this).toMono().awaitSingleOrNull()?.id.run {
                    if (this != null) authorities?.map {
                        insert(AccountAuthorityEntity(userId = this, role = it.role)).awaitSingle()
                    }
                }
            }
        }
    }

    override suspend fun findActivationKeyByLogin(login: String): String? =
        dao.select<AccountEntity>().matching(query(where("login").`is`(login)))
            .awaitOneOrNull()?.activationKey


    override suspend fun findOneByActivationKey(key: String): AccountCredentials? =
        dao.select<AccountEntity>().matching(query(where("activationKey").`is`(key)))
            .awaitOneOrNull()?.toCredentialsModel()
}

@Repository
class AuthorityRepositoryR2dbc(
    private val repository: R2dbcEntityTemplate
) : AuthorityRepository {
    override suspend fun findOne(role: String): String? =
        repository.select(AuthorityEntity::class.java)
            .matching(query(where(ROLE_COLUMN).`is`(role)))
            .awaitOneOrNull()?.role
}


//@Repository
//class AccountRepositoryInMemory(
//    private val accountAuthorityRepository: AccountAuthorityRepository,
//    private val authorityRepository: AuthorityRepository
//) : AccountRepository {
//
//    companion object {
//        private val accounts by lazy { mutableSetOf<AccountRecord<AuthorityRecord>>() }
//    }
//
//    override suspend fun findOneByLogin(login: String): AccountCredentials? =
//        accounts.find { login.equals(it.login,  true) }?.toCredentialsModel()
//
//    override suspend fun findOneByEmail(email: String): AccountCredentials? =
//        accounts.find { email.equals(it.email,  true) }?.toCredentialsModel()
//
//
//    override suspend fun save(model: AccountCredentials): Account? =
//        create(model).run {
//            when {
//                this != null -> return@run this
//                else -> update(model)
//            }
//        }
//
//    private fun create(model: AccountCredentials) =
//        if (`mail & login do not exist`(model))
//            model.copy(id = UUID.randomUUID()).apply {
//                @Suppress("UNCHECKED_CAST")
//                accounts += AccountEntity(this) as AccountRecord<AuthorityRecord>
//            }.toAccount() else null
//
//    private fun `mail & login do not exist`(model: AccountCredentials) =
//        accounts.none {
//            it.login.equals(model.login,  true)
//                    && it.email.equals(model.email,  true)
//        }
//
//
//    private fun `mail exists and login exists`(model: AccountCredentials) =
//        accounts.any {
//            model.email.equals(it.email,  true)
//                    && model.login.equals(it.login,  true)
//        }
//
//
//    private fun `mail exists and login does not`(model: AccountCredentials) =
//        accounts.any {
//            model.email.equals(it.email,  true)
//                    && !model.login.equals(it.login,  true)
//        }
//
//
//    private fun `mail does not exist and login exists`(model: AccountCredentials) =
//        accounts.any {
//            !model.email.equals(it.email,  true)
//                    && model.login.equals(it.login,  true)
//        }
//
//    private fun update(
//        model: AccountCredentials,
//    ): Account? = when {
//        `mail exists and login does not`(model) -> changeLogin(model)?.run { patch(this) }
//        `mail does not exist and login exists`(model) -> changeEmail(model)?.run { patch(this) }
//        `mail exists and login exists`(model) -> patch(model)
//        else -> null
//    }
//
//    private fun changeLogin(
//        model: AccountCredentials,
//    ): AccountCredentials? =
//        try {
//            @Suppress("CAST_NEVER_SUCCEEDS")
//            (accounts.first { model.email.equals(it.email,  true) } as AccountCredentials).run {
//                val retrieved: AccountCredentials = copy(login = model.login)
//                accounts.remove(this as AccountRecord<AuthorityRecord>?)
//                (retrieved as AccountRecord<AuthorityRecord>?)?.run { accounts.add(this) }
//                model
//            }
//        } catch (_: NoSuchElementException) {
//            null
//        }
//
//
//    private fun changeEmail(
//        model: AccountCredentials,
//    ): AccountCredentials? = try {
//        @Suppress("CAST_NEVER_SUCCEEDS")
//        (accounts.first { model.login.equals(it.login,  true) } as AccountCredentials).run {
//            val retrieved: AccountCredentials = copy(email = model.email)
//            accounts.remove(this as AccountRecord<AuthorityRecord>?)
//            (retrieved as AccountRecord<AuthorityRecord>?)?.run { accounts.add(this) }
//            model
//        }
//    } catch (_: NoSuchElementException) {
//        null
//    }
//
//    private fun patch(
//        model: AccountCredentials?,
//    ): Account? =
//        model.run {
//            val retrieved = accounts.find { this?.email?.equals(it.email,  true)!! }
//            accounts.remove(accounts.find { this?.email?.equals(it.email,  true)!! })
//            ((retrieved?.toCredentialsModel())?.copy(
//                password = `if password is null or empty then no change`(model, retrieved.toCredentialsModel()),
//                activationKey = `switch activationKey case then patch`(model, retrieved.toCredentialsModel()),
//                authorities = `if authorities are null or empty then no change`(model, retrieved.toCredentialsModel())
//            ).apply {
//                @Suppress("UNCHECKED_CAST")
//                accounts.add(this?.let { AccountEntity(it) } as AccountRecord<AuthorityRecord>)
//            }?.toAccount())
//        }
//
//
//    private fun `if password is null or empty then no change`(
//        model: AccountCredentials?,
//        retrieved: AccountCredentials
//    ): String = when {
//        model == null -> retrieved.password!!
//        model.password == null -> retrieved.password!!
//        model.password.isNotEmpty() -> model.password
//        else -> retrieved.password!!
//    }
//
//    @Suppress("FunctionName")
//    private fun `switch activationKey case then patch`(
//        model: AccountCredentials?,
//        retrieved: AccountCredentials
//    ): String? = when {
//        model == null -> null
//        model.activationKey == null -> null
//        !retrieved.activated
//                && retrieved.activationKey.isNullOrBlank()
//                && model.activationKey.isNotEmpty() -> model.activationKey
//
//        !retrieved.activated
//                && !retrieved.activationKey.isNullOrBlank()
//                && model.activationKey.isNotEmpty() -> retrieved.activationKey
//
//        else -> null
//    }
//
//    private fun `if authorities are null or empty then no change`(
//        model: AccountCredentials?,
//        retrieved: AccountCredentials
//    ): Set<String> {
//        if (model != null) {
//            if (model.authorities != null) {
//                if (model.authorities.isNotEmpty() && model.authorities.contains(ROLE_USER))
//                    return model.authorities
//                if (retrieved.authorities != null)
//                    if (retrieved.authorities.isNotEmpty()
//                        && retrieved.authorities.contains(ROLE_USER)
//                        && !model.authorities.contains(ROLE_USER)
//                    ) return retrieved.authorities
//            } else
//                if (!retrieved.authorities.isNullOrEmpty()
//                    && retrieved.authorities.contains(ROLE_USER)
//                ) return retrieved.authorities
//        }
//        return emptySet()
//    }
//
//
//    override suspend fun delete(account: Account) {
//        accounts.apply { if (isNotEmpty()) remove(find { it.id == account.id }) }
//    }
//
//    override suspend fun findActivationKeyByLogin(login: String): String? =
//        accounts.find {
//            it.login.equals(login,  true)
//        }?.activationKey
//
//    override suspend fun count(): Long = accounts.size.toLong()
//    override suspend fun suppress(account: Account) {
//        accountAuthorityRepository.deleteAllByAccountId(account.id!!)
//        delete(account)
//    }
//
//    override suspend fun signup(model: AccountCredentials) {
//        accountAuthorityRepository.save(save(model)?.id!!, ROLE_USER)
//    }
//
//    override suspend fun findOneActivationKey(key: String): AccountCredentials? {
//        TODO("Not yet implemented")
//    }
//}
//
//
//@Repository
//class AccountAuthorityRepositoryInMemory : AccountAuthorityRepository {
//    companion object {
//        private val accountAuthorities by lazy { mutableSetOf<AccountAuthorityEntity>() }
//    }
//
//    override suspend fun count(): Long = accountAuthorities.size.toLong()
//
//    override suspend fun save(id: UUID, authority: String) {
//        accountAuthorities.add(AccountAuthorityEntity(userId = id, role = authority))
//    }
//
//
//    override suspend fun delete(id: UUID, authority: String): Unit =
//        accountAuthorities.run {
//            filter { it.userId == id && it.role == authority }
//                .map { remove(it) }
//        }
//
//
//    override suspend fun deleteAll() = accountAuthorities.clear()
//
//
//    override suspend fun deleteAllByAccountId(id: UUID): Unit =
//        accountAuthorities.run {
//            filter { it.userId == id }
//                .map { remove(it) }
//        }
//
//}
