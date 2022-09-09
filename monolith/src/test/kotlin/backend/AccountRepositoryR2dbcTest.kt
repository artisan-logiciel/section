@file:Suppress("NonAsciiCharacters", "unused")

package backend

import backend.data.Data
import backend.tdd.testLoader
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.r2dbc.core.*
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Repository
import reactor.kotlin.core.publisher.toMono
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

@Repository
class AccountAuthorityRepositoryR2dbc(
    private val dao: R2dbcEntityTemplate,
) : AccountAuthorityRepository {
    override suspend fun save(id: UUID, authority: String) {
        dao.insert(AccountAuthorityEntity(userId = id, role = authority)).awaitSingle()
    }

    override suspend fun delete(id: UUID, authority: String) {
        dao.delete(AccountAuthorityEntity(userId = id, role = authority)).awaitSingle()
    }

    override suspend fun count(): Long = dao.select<AccountAuthorityEntity>().count().awaitSingle()


    override suspend fun deleteAll() {
        dao.delete<AccountAuthorityEntity>().allAndAwait()
    }

    override suspend fun deleteAllByAccountId(id: UUID) {
        dao.delete<AccountAuthorityEntity>().matching(query(where("userId").`is`(id))).allAndAwait()
    }

}


@Repository
class AccountRepositoryR2dbc(
    private val dao: R2dbcEntityTemplate,
//    private val authorityRepository: AuthorityRepository
) : AccountRepository {
    override suspend fun save(model: AccountCredentials): Account? =
        dao.insert(AccountEntity(model)).awaitSingleOrNull()?.toModel()

    override suspend fun count(): Long = dao.select<AccountEntity>().count().awaitSingle()

    override suspend fun delete(account: Account) {
        dao.delete(account).awaitSingleOrNull()
    }

    override suspend fun findOneByLogin(login: String): AccountCredentials? =
        dao.select<AccountEntity>().matching(query(where("login").`is`(login))).awaitOneOrNull()?.toCredentialsModel()


    override suspend fun findOneByEmail(email: String): AccountCredentials? =
        dao.select<AccountEntity>().matching(query(where("email").`is`(email))).awaitOneOrNull()?.toCredentialsModel()

    override suspend fun suppress(account: Account) {
        dao.run {
            delete<AccountAuthorityEntity>().matching(query(where("userId").`is`(account.id!!))).toMono().awaitSingle()
            delete<AccountEntity>().toMono().awaitSingle()
        }
    }


    override suspend fun signup(model: AccountCredentials) {
        dao.run {
            AccountEntity(model).run {
                val id = insert(this).toMono().awaitSingleOrNull()?.id
                if (id != null) authorities?.map {
                    insert(AccountAuthorityEntity(userId = id, role = it.role)).awaitSingle()
                }
            }

        }
    }

    override suspend fun findActivationKeyByLogin(login: String): String? =
        dao.select<AccountEntity>().matching(query(where("login").`is`(login))).awaitOneOrNull()?.activationKey


    override suspend fun findOneActivationKey(key: String): AccountCredentials? =
        dao.select<AccountEntity>().matching(query(where("activationKey").`is`(key))).awaitOneOrNull()
            ?.toCredentialsModel()
}

internal fun createAccounts(accounts: Set<AccountCredentials>, repository: R2dbcEntityTemplate) {
    assertEquals(0, repository.select<AccountEntity>().count().block())
    accounts.map { repository.insert(AccountEntity(it)).block() }
    assertEquals(accounts.size.toLong(), repository.select<AccountEntity>().count().block())
}

internal suspend fun deleteAccounts(repository: R2dbcEntityTemplate) {
    if (repository.select<AccountEntity>().count().block()!! > 0) mono {
        repository.databaseClient.run {
            sql("DELETE FROM user_authority").toMono().block()
            sql("DELETE FROM `user`").toMono().block()
        }
    }
}

internal class AccountRepositoryR2dbcTest {
    private lateinit var context: ConfigurableApplicationContext

    private val repository: R2dbcEntityTemplate by lazy { context.getBean() }
    private val accountRepository: AccountRepository by lazy { context.getBean<AccountRepositoryR2dbc>() }

    @BeforeAll
    fun `lance le server en profile test`() = runApplication<Server> { testLoader(this) }.run { context = this }

    @AfterAll
    fun `arrête le serveur`() = context.close()


    @AfterEach
    fun tearDown() = deleteAccounts(repository)


    @Test
    fun save() = runBlocking {
        assertEquals(0, repository.select<AccountEntity>().count().block())
        accountRepository.save(Data.defaultAccount)
        assertEquals(1, repository.select<AccountEntity>().count().block())
    }

    @Test
    fun count() {

    }

    @Test
    fun delete() {
    }

    @Test
    fun findOneByEmail() {
    }

    @Test
    fun findOneByLogin(): Unit = runBlocking {}

    @Test
    fun suppress() {
    }

    @Test
    fun signup() {
    }

    @Test
    fun findActivationKeyByLogin() {
    }

    @Test
    fun findOneActivationKey() {
    }
}