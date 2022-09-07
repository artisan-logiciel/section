@file:Suppress("NonAsciiCharacters")

package backend

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository

@Repository
abstract class AccountRepositoryR2dbc(
    private val repository: R2dbcEntityTemplate
) : AccountRepository

internal class AccountRepositoryR2dbcTest {
//    private lateinit var context: ConfigurableApplicationContext
//    private val authorityRepository: AuthorityRepositoryR2dbc by lazy { context.getBean() }
//
//    @BeforeAll
//    fun `lance le server en profile test`() =
//        runApplication<Server> { testLoader(app = this) }
//            .run { context = this }
//
//    @AfterAll
//    fun `arrête le serveur`() = context.close()
//
//    @Test
//    fun findOne(): Unit = runBlocking {
//        mapOf(
//            ROLE_ADMIN to ROLE_ADMIN,
//            ROLE_USER to ROLE_USER,
//            ROLE_ANONYMOUS to ROLE_ANONYMOUS,
//            "" to null,
//            "foo" to null
//        ).map { assertEquals(it.value, authorityRepository.findOne(it.key)) }
//    }
}