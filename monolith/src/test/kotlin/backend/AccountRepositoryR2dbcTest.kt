@file:Suppress("NonAsciiCharacters", "unused")

package backend

import backend.data.Data
import backend.tdd.testLoader
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.select
import kotlin.test.Test
import kotlin.test.assertEquals

class AccountRepositoryR2dbc(
    private val repository: R2dbcEntityTemplate,
    private val authorityRepository: AuthorityRepository
) : AccountRepository {
    override suspend fun save(model: AccountCredentials): Account? =
        // TODO("Not yet implemented")
    null

    override suspend fun count(): Long
        // TODO("Not yet implemented")
    =-1L

    override suspend fun delete(account: Account) {
        // TODO("Not yet implemented")
    }

    override suspend fun findOneByLogin(login: String): AccountCredentials? =
        // TODO("Not yet implemented")
    null

    override suspend fun findOneByEmail(email: String): AccountCredentials? =
        // TODO("Not yet implemented")
    null

    override suspend fun suppress(account: Account) {
        // TODO("Not yet implemented")
    }


    override suspend fun signup(model: AccountCredentials) {
        // TODO("Not yet implemented")
    }

    override suspend fun findActivationKeyByLogin(login: String): String? =
        // TODO("Not yet implemented")
    null

    override suspend fun findOneActivationKey(key: String): AccountCredentials? =
        // TODO("Not yet implemented")
    null
}

internal fun createAccounts(accounts: Set<AccountCredentials>, repository: R2dbcEntityTemplate) {
    // TODO("Not yet implemented")
}

internal fun deleteAccounts(accounts: Set<AccountCredentials>, repository: R2dbcEntityTemplate) {
    // TODO("Not yet implemented")
}

internal class AccountRepositoryR2dbcTest {
    private lateinit var context: ConfigurableApplicationContext

    private val repository: R2dbcEntityTemplate by lazy { context.getBean() }

    @BeforeAll
    fun `lance le server en profile test`() =
        runApplication<Server> { testLoader(this) }
            .run { context = this }

    @AfterAll
    fun `arrête le serveur`() = context.close()


    @AfterEach
    fun tearDown() = deleteAccounts(Data.accounts, repository)


    @Test
    fun save() {
        assertEquals(0, repository.select<AccountEntity>().count().block())
        repository.insert(Data.defaultAccountEntity).block()
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
    fun findOneByLogin(): Unit = runBlocking {
    }

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