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
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals


internal class AccountRepositoryR2dbcTest {
    private lateinit var context: ConfigurableApplicationContext

    private val dao: R2dbcEntityTemplate by lazy { context.getBean() }
    private val accountRepository: AccountRepository by lazy { context.getBean<AccountRepositoryR2dbc>() }

    @BeforeAll
    fun `lance le server en profile test`() = runApplication<Server> { testLoader(this) }.run { context = this }

    @AfterAll
    fun `arrête le serveur`() = context.close()


    @AfterEach
    fun tearDown() = deleteAllAccounts(dao)


    @Test
    fun save() {
        mono {
            val countBefore = countAccount(dao)
            assertEquals(0, countBefore)
            accountRepository.save(Data.defaultAccount)
            assertEquals(countBefore + 1, countAccount(dao))
        }
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