@file:Suppress(
    "NonAsciiCharacters", "unused"
)

package backend

import backend.Log.log
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals


internal class ActivateAccountControllerTest {

    companion object {
        private const val SIGNUP_URI = "api/activate?key="
        private const val BASE_URL = "http://localhost:8080/"

    }

    private val client: WebTestClient by lazy {
        WebTestClient
            .bindToServer()
            .baseUrl(BASE_URL)
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

    @Test
    fun `vérifie que la requete contient bien des données cohérentes`() {
        RandomUtils.generateActivationKey.run {
            client
                .get()
                .uri("/api/activate?key={activationKey}", this)
                .exchange()
                .expectStatus()
                .is5xxServerError
                .returnResult<Unit>().url.let {
                    assertEquals(URI("$BASE_URL$SIGNUP_URI?key=$this"), it)
                }

        }
    }

//    /*
//        @Test
//        void testActivateAccount() {
//            final String activationKey = "some activation key";
//            User user = new User();
//            user.setLogin("activate-account");
//            user.setEmail("activate-account@example.com");
//            user.setPassword(RandomStringUtils.random(60));
//            user.setActivated(false);
//            user.setActivationKey(activationKey);
//            user.setCreatedBy(Constants.SYSTEM);
//
//            userRepository.save(user).block();
//
//            accountWebTestClient.get().uri("/api/activate?key={activationKey}", activationKey).exchange().expectStatus().isOk();
//
//            user = userRepository.findOneByLogin(user.getLogin()).block();
//            assertThat(user.isActivated()).isTrue();
//        }
//    */
//
//    /*
//        @Test
//        void testActivateAccountWithWrongKey() {
//            accountWebTestClient
//                .get()
//                .uri("/api/activate?key=wrongActivationKey")
//                .exchange()
//                .expectStatus()
//                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    */


}