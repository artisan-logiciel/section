@file:Suppress("NonAsciiCharacters")

package backend

import backend.Constants.ROLE_ADMIN
import backend.Constants.ROLE_ANONYMOUS
import backend.Constants.ROLE_USER
import backend.tdd.testLoader
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext

internal class AuthorityRepositoryR2dbcTest {
    private lateinit var context: ConfigurableApplicationContext
    private val authorityRepository: AuthorityRepositoryR2dbc by lazy { context.getBean() }

    @BeforeAll
    fun `lance le server en profile test`() =
        runApplication<Server> { testLoader(app = this) }
            .run { context = this }

    @AfterAll
    fun `arrête le serveur`() = context.close()

    @Test
    fun findOne(): Unit = runBlocking {
        mapOf(
            ROLE_ADMIN to ROLE_ADMIN,
            ROLE_USER to ROLE_USER,
            ROLE_ANONYMOUS to ROLE_ANONYMOUS,
            "" to null,
            "foo" to null
        ).map { assertEquals(it.value, authorityRepository.findOne(it.key)) }
    }
}