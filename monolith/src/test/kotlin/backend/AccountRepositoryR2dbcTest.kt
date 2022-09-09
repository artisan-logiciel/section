@file:Suppress("NonAsciiCharacters", "unused")

package backend

import backend.data.Data
import backend.tdd.testLoader
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import kotlin.test.Test
import org.springframework.beans.factory.getBean

class AccountRepositoryR2dbc(
    private val repository: R2dbcEntityTemplate,
    private val authorityRepository: AuthorityRepository
) : AccountRepository {
    override suspend fun save(model: AccountCredentials): Account? {
        TODO("Not yet implemented")
    }
    override suspend fun count(): Long {
        TODO("Not yet implemented")
    }
    override suspend fun delete(account: Account) {
        TODO("Not yet implemented")
    }
    override suspend fun findOneByLogin(login: String): AccountCredentials? {
        TODO("Not yet implemented")
    }

    override suspend fun findOneByEmail(email: String): AccountCredentials? {
        TODO("Not yet implemented")
    }

    override suspend fun suppress(account: Account) {
        TODO("Not yet implemented")
    }


    override suspend fun signup(model: AccountCredentials) {
        TODO("Not yet implemented")
    }

    override suspend fun findActivationKeyByLogin(login: String): String? {
        TODO("Not yet implemented")
    }

    override suspend fun findOneActivationKey(key: String): AccountCredentials? {
        TODO("Not yet implemented")
    }
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

    @BeforeEach
    fun setUp() {
        createAccounts(Data.accounts,repository)
    }

    private fun createAccounts(accounts: Set<AccountCredentials>, repository: R2dbcEntityTemplate) {
        TODO("Not yet implemented")
    }

    @AfterEach
    fun tearDown() {
        deleteAccounts(Data.accounts,repository)
    }

    private fun deleteAccounts(accounts: Set<AccountCredentials>,repository: R2dbcEntityTemplate) {
        TODO("Not yet implemented")
    }

    @Test
    fun save() {
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