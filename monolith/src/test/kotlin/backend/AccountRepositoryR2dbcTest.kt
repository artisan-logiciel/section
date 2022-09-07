@file:Suppress("NonAsciiCharacters", "unused")

package backend

import backend.tdd.testLoader
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import kotlin.test.Test

@Repository
abstract class AccountRepositoryR2dbc(
    private val repository: R2dbcEntityTemplate,
    private val authorityRepository: AuthorityRepository
) : AccountRepository

internal class AccountRepositoryR2dbcTest {
    private lateinit var context: ConfigurableApplicationContext

    private val accountRepository: AccountRepository by lazy { context.getBean<AccountRepositoryR2dbc>() }

    @BeforeAll
    fun `lance le server en profile test`() =
        runApplication<Server> { testLoader(app = this) }
            .run { context = this }

    @AfterAll
    fun `arrête le serveur`() = context.close()

    @Test
    fun findOneByLogin(): Unit = runBlocking {
    }
}