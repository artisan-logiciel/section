@file:Suppress("NonAsciiCharacters")

package backend

import backend.tdd.testLoader
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate

internal class AccountAuthorityRepositoryR2dbcTest {

    private lateinit var context: ConfigurableApplicationContext

    private val dao: R2dbcEntityTemplate by lazy { context.getBean() }
    private val accountAuthorityRepository: AccountAuthorityRepository by lazy { context.getBean<AccountAuthorityRepositoryR2dbc>() }

    @BeforeAll
    fun `lance le server en profile test`() = runApplication<Server> {
        testLoader(this)
    }.run { context = this }

    @AfterAll
    fun `arrête le serveur`() = context.close()


    @AfterEach
    fun tearDown() = deleteAllAccounts(dao)

    @Test
    fun test_save() {
    }

    @Test
    fun test_count() {
    }

    @Test
    fun test_delete() {
    }

    @Test
    fun test_deleteAll() {
    }

    @Test
    fun test_deleteAllByAccountId() {
    }
}