@file:Suppress("NonAsciiCharacters")

package backend

import backend.Constants.ROLE_ADMIN
import backend.Constants.ROLE_ANONYMOUS
import backend.Constants.ROLE_USER
import backend.tdd.testLoader
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AuthorityRepositoryR2dbcTest {
    private lateinit var context: ConfigurableApplicationContext

    private val authorityRepository: AuthorityRepository by lazy { context.getBean<AuthorityRepositoryR2dbc>() }

    @BeforeAll
    fun `lance le server en profile test`() = runApplication<Server> {
        testLoader(app = this)
    }.run { context = this }

    @AfterAll
    fun `arrête le serveur`() = context.close()

    @Test
    fun test_findOne(): Unit = runBlocking {
        mapOf(
            ROLE_ADMIN to ROLE_ADMIN,
            ROLE_USER to ROLE_USER,
            ROLE_ANONYMOUS to ROLE_ANONYMOUS,
            "" to null,
            "foo" to null
        ).map { assertEquals(it.value, authorityRepository.findOne(it.key)) }
    }
}