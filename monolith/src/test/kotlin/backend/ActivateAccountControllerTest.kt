@file:Suppress(
    "NonAsciiCharacters", "unused"
)

package backend

import backend.tdd.testLoader
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.web.reactive.server.WebTestClient


internal class ActivateAccountControllerTest {

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
        private val accountRepository: AccountRepository by lazy { context.getBean() }
        private val accountAuthorityRepository: IAccountAuthorityRepository by lazy { context.getBean() }
    }

    @BeforeAll
    fun `lance le server en profile test`() =
        runApplication<Server> { testLoader(app = this) }
            .run { context = this }

    @AfterAll
    fun `arrête le serveur`() = context.close()


//    @Test//TODO: revisiter avec coherence de la requete
//    fun `vérifie que la requete contient bien des données cohérentes`() {
//        client
//            .post()
//            .uri("/api/foo")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(defaultAccount)
//            .exchange()
//            .returnResult<Unit>().run {
//                assertTrue(requestBodyContent!!.isNotEmpty())
//                requestBodyContent
//                    ?.map { it.toInt().toChar().toString() }
//                    ?.reduce { acc: String, s: String -> acc + s }.apply requestContent@{
//                        //test request contains passed values
//                        defaultAccount.run {
//                            setOf(
//                                "\"login\":\"${login}\"",
//                                "\"password\":\"${password}\"",
//                                "\"firstName\":\"${firstName}\"",
//                                "\"lastName\":\"${lastName}\"",
//                                "\"email\":\"${email}\"",
//                                "\"imageUrl\":\"${imageUrl}\""
//                            ).map { assertTrue(this@requestContent?.contains(it) ?: false) }
//                        }
//                    }
//            }
//    }

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