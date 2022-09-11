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
            query(where("userId").`is`(id).and(where("role").`is`(authority).ignoreCase(true))),
            AccountAuthorityEntity::class.java
        ).awaitSingleOrNull().run {
            if (this != null && this.id != null)
                dao.delete(this)
                    .awaitSingle()
        }
    }

    override suspend fun count(): Long = dao.select<AccountAuthorityEntity>().count()
        .awaitSingle()

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
        dao.select<AccountEntity>().matching(query(where("login").`is`(login).ignoreCase(true)))
            .awaitOneOrNull()?.toCredentialsModel()


    override suspend fun findOneByEmail(email: String): AccountCredentials? =
        dao.select<AccountEntity>().matching(query(where("email").`is`(email).ignoreCase(true)))
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
        dao.select<AccountEntity>().matching(query(where("login").`is`(login).ignoreCase(true)))
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

