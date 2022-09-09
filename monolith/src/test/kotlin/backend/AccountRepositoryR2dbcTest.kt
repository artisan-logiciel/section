@file:Suppress("NonAsciiCharacters", "unused")

package backend

import backend.data.Data
import backend.tdd.testLoader
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.select
import reactor.kotlin.core.publisher.toMono
import kotlin.test.Test
import kotlin.test.assertEquals

class AccountRepositoryR2dbc(
    private val repository: R2dbcEntityTemplate, private val authorityRepository: AuthorityRepository
) : AccountRepository {
    override suspend fun save(model: AccountCredentials): Account? = null

    override suspend fun count(): Long = -1L

    override suspend fun delete(account: Account) {

    }

    override suspend fun findOneByLogin(login: String): AccountCredentials? = null

    override suspend fun findOneByEmail(email: String): AccountCredentials? = null

    override suspend fun suppress(account: Account) {

    }


    override suspend fun signup(model: AccountCredentials) {

    }

    override suspend fun findActivationKeyByLogin(login: String): String? = null

    override suspend fun findOneActivationKey(key: String): AccountCredentials? = null
}

internal fun createAccounts(accounts: Set<AccountCredentials>, repository: R2dbcEntityTemplate) {
    assertEquals(0, repository.select<AccountEntity>().count().block())
    accounts.map { repository.insert(AccountEntity(it)).block() }
    assertEquals(accounts.size.toLong(), repository.select<AccountEntity>().count().block())
}

internal fun deleteAccounts(repository: R2dbcEntityTemplate) {
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

    @BeforeAll
    fun `lance le server en profile test`() = runApplication<Server> { testLoader(this) }.run { context = this }

    @AfterAll
    fun `arrête le serveur`() = context.close()


    @AfterEach
    fun tearDown() = deleteAccounts(repository)


    @Test
    fun save() = runBlocking {
        assertEquals(0, repository.select<AccountEntity>().count().block())
        repository.insert(AccountEntity(Data.defaultAccount)).block()
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