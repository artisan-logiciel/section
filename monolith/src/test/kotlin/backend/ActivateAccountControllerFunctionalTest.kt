@file:Suppress(
    "NonAsciiCharacters", "unused"
)

package backend

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.web.reactive.server.WebTestClient


internal class ActivateAccountControllerFunctionalTest {

    companion object {
        private const val SIGNUP_URI = "api/activate"
        private const val BASE_URL = "http://localhost:8080"
        private val client: WebTestClient by lazy {
            WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
        }
        private lateinit var context: ConfigurableApplicationContext
        private val accountRepository: IAccountModelRepository by lazy { context.getBean() }
        private val accountAuthorityRepository: IAccountAuthorityRepository by lazy { context.getBean() }
    }

    @BeforeAll
    fun `lance le server en profile test`() =
        runApplication<Server> { testLoader(app = this) }
            .run { context = this }

    @AfterAll
    fun `arrête le serveur`() = context.close()

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