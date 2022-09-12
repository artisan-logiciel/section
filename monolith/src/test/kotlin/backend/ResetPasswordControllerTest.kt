@file:Suppress(
    "NonAsciiCharacters", "unused"
)

package backend

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.test.web.reactive.server.WebTestClient
//import org.springframework.test.web.reactive.server.returnResult
//import java.net.URI
//import kotlin.test.*

internal class ResetPasswordControllerTest {

    companion object {
        private const val ACTIVATE_URI = "api/account/activate?key="
        private const val ACTIVATE_URI_KEY_PARAM = "{activationKey}"

    }

    private val client: WebTestClient by lazy {
        WebTestClient
            .bindToServer()
            .baseUrl(BASE_URL_DEV)
            .build()
    }
    private lateinit var context: ConfigurableApplicationContext
    private val dao: R2dbcEntityTemplate by lazy { context.getBean() }

    @BeforeAll
    fun `lance le server en profile test`() =
        runApplication<Server> { testLoader(app = this) }
            .run { context = this }

    @AfterAll
    fun `arrête le serveur`() = context.close()

    @AfterEach
    fun tearDown() = deleteAllAccounts(dao)

//    @Test
//    fun `vérifie que la requete contient bien des données cohérentes`() {
//        RandomUtils.generateActivationKey.run {
//            client
//                .get()
//                .uri("$SIGNUP_URI$SIGNUP_URI_KEY_PARAM", this)
//                .exchange()
//                .returnResult<Unit>().url.let {
//                    assertEquals(URI("$BASE_URL_DEV$SIGNUP_URI$this"), it)
//                }
//
//        }
//    }
//
//    @Test
//    fun `test activate avec une mauvaise clé`() {
//        client
//            .get()
//            .uri("$SIGNUP_URI$SIGNUP_URI_KEY_PARAM", "wrongActivationKey")
//            .exchange()
//            .expectStatus()
//            .is5xxServerError
//            .returnResult<Unit>()
//
//
//    }
//
//    @Test
//    fun `test activate avec une clé valide`() {
//        assertEquals(0, countAccount(dao))
//        assertEquals(0, countAccountAuthority(dao))
//        createDataAccounts(setOf(Data.defaultAccount), dao)
//        assertEquals(1, countAccount(dao))
//        assertEquals(1, countAccountAuthority(dao))
//
//        val validActivationKey = findOneByLogin(Data.defaultAccount.login!!, dao)!!.apply {
//            assertTrue(activationKey!!.isNotBlank())
//            assertFalse(activated)
//        }.activationKey
//
//        client
//            .get()
//            .uri(
//                "$SIGNUP_URI$SIGNUP_URI_KEY_PARAM",
//                validActivationKey
//            )
//            .exchange()
//            .expectStatus().isOk
//            .returnResult<Unit>()
//
//        findOneByLogin(Data.defaultAccount.login!!, dao)!!.run {
//            assertNull(activationKey)
//            assertTrue(activated)
//        }
//    }
}