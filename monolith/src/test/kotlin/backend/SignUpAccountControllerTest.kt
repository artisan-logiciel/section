@file:Suppress(
    "NonAsciiCharacters", "unused", "GrazieInspection"
)

package backend

import backend.Constants.ROLE_USER
import backend.Log.log
import backend.data.Data.defaultAccount
import backend.data.Data.defaultUser
import backend.tdd.testLoader
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import kotlin.test.*

/**
 *
#language: fr
#noinspection CucumberUndefinedStep
Fonctionnalité: Inscription d'un compte utilisateur.

Contexte:
Etant donné une liste de login, email, password, firstName, lastName

| login | email          | password | firstName | lastName |
| admin | admin@acme.com | admin    | admin     | admin    |
| user  | user@acme.com  | user     | user      | user     |
| test1 | test1@acme.com | test1    | test1     | test1    |
| test2 | test2@acme.com | test2    | test2     | test2    |
| test3 | test3@acme.com | test3    | test3     | test3    |

Scénario: Création d'un nouveau compte utilisateur.
Etant donné l'utilisateur de la liste qui a pour login "user"
Quand on envoie la requête d'inscription de "user"
Alors le résultat est la création d'un nouveau compte non activé
 *
 */
internal class SignUpAccountControllerTest {

    companion object {
        private const val SIGNUP_URI = "api/signup"
        private const val BASE_URL = "http://localhost:8080"
    }

    private lateinit var context: ConfigurableApplicationContext
    private val client: WebTestClient by lazy {
        WebTestClient
            .bindToServer()
            .baseUrl(BASE_URL)
            .build()
    }

    //En controlant l'implementation désiré
//    private val accountRepository: AccountRepositoryInMemory by lazy { context.getBean() }
//    private val accountAuthorityRepository: AccountAuthorityRepositoryInMemory by lazy { context.getBean() }
    //En laissant le conteneur injecter la dépendance
    private val accountRepository: AccountRepository by lazy { context.getBean() }
    private val accountAuthorityRepository: AccountAuthorityRepository by lazy { context.getBean() }

    @BeforeAll
    fun `lance le server en profile test`() =
        runApplication<Server> { testLoader(app = this) }
            .run { context = this }

    @AfterAll
    fun `arrête le serveur`() = context.close()


    @Test
    fun `vérifie que la requête contient bien des données cohérentes`() {
        client
            .post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(defaultAccount)
            .exchange()
            .returnResult<Unit>().run {
                assertTrue(requestBodyContent!!.isNotEmpty())
                requestBodyContent
                    ?.map { it.toInt().toChar().toString() }
                    ?.reduce { acc: String, s: String -> acc + s }.apply requestContent@{
                        //test request contains passed values
                        defaultAccount.run {
                            setOf(
                                "\"login\":\"${login}\"",
                                "\"password\":\"${password}\"",
                                "\"firstName\":\"${firstName}\"",
                                "\"lastName\":\"${lastName}\"",
                                "\"email\":\"${email}\"",
                            ).map { assertTrue(this@requestContent?.contains(it) ?: false) }
                        }
                    }
            }
    }

    @Test
    fun `signup avec un account valide`(): Unit = runBlocking {
        val countUserBefore = accountRepository.count()
        val countUserAuthBefore = accountAuthorityRepository.count()
        assertEquals(0, countUserBefore)
        assertEquals(0, countUserAuthBefore)
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(defaultAccount)
            .exchange()
            .expectStatus()
            .isCreated
            .returnResult<Unit>()
            .run { responseBodyContent?.isEmpty()?.let { assertTrue(it) } }
        assertEquals(countUserBefore + 1, accountRepository.count())
        assertEquals(countUserAuthBefore + 1, accountAuthorityRepository.count())
        assertFalse(accountRepository.findOneByEmail(defaultAccount.email!!)!!.activated)
        //clean accounts and accountAuthorities after test
        accountRepository.findOneByLogin(defaultAccount.login!!).run {
            accountAuthorityRepository.deleteAllByAccountId(this?.id!!)
            accountRepository.delete(this.toAccount())
        }

        assertEquals(countUserAuthBefore, accountAuthorityRepository.count())
        assertEquals(countUserBefore, accountRepository.count())
    }

    @Test
    fun `test register account avec login invalid`(): Unit = runBlocking {
        assertEquals(0, accountRepository.count())
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(defaultUser.copy(login = "funky-log(n"))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
            .run { responseBodyContent?.isNotEmpty()?.let { assertTrue(it) } }
        assertEquals(accountRepository.count(), 0)
    }


    @Test
    fun `test register account avec un email invalid`(): Unit = runBlocking {
        assertEquals(0, accountRepository.count())
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(defaultAccount.copy(password = "inv"))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
            .run { responseBodyContent?.isNotEmpty()?.let { assertTrue(it) } }
        assertEquals(0, accountRepository.count())
    }

    @Test
    fun `test register account avec un password invalid`(): Unit = runBlocking {
        assertEquals(0, accountRepository.count())
        client.post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(defaultAccount.copy(password = "123"))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
            .run { responseBodyContent?.isNotEmpty()?.let { assertTrue(it) } }
        assertEquals(0, accountRepository.count())
    }

    @Test
    fun `test register account avec un password null`() = runBlocking {
        assertEquals(0, accountRepository.count())
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(defaultAccount.copy(password = null))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
            .run { responseBodyContent?.isNotEmpty()?.let { assertTrue(it) } }
        assertEquals(0, accountRepository.count())
    }

    @Test
    fun `test register account activé avec un email existant`(): Unit = runBlocking {
        assertEquals(0, accountRepository.count())
        assertEquals(0, accountAuthorityRepository.count())
        accountAuthorityRepository.save(
            accountRepository.save(defaultAccount.copy(activated = true))?.id!!,
            ROLE_USER
        )
        assertEquals(1, accountRepository.count())
        assertEquals(1, accountAuthorityRepository.count())
        assertTrue(accountRepository.findOneByEmail(defaultAccount.email!!)!!.activated)

        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(defaultAccount.copy(login = "foo"))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
            .run { responseBodyContent?.isNotEmpty()?.let { assertTrue(it) } }

        accountRepository.findOneByLogin(defaultAccount.login!!).run {
            accountAuthorityRepository.deleteAllByAccountId(this?.id!!)
            accountRepository.delete(this.toAccount())
        }
        assertEquals(0, accountAuthorityRepository.count())
        assertEquals(0, accountRepository.count())
    }

    @Test
    fun `test register account activé avec un login existant`(): Unit = runBlocking {
        assertEquals(0, accountRepository.count())
        assertEquals(0, accountAuthorityRepository.count())
        accountAuthorityRepository.save(
            accountRepository.save(defaultAccount.copy(activated = true))?.id!!,
            ROLE_USER
        )
        assertTrue(accountRepository.findOneByEmail(defaultAccount.email!!)!!.activated)
        assertEquals(1, accountRepository.count())
        assertEquals(1, accountAuthorityRepository.count())

        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(defaultAccount.copy(email = "foo@localhost"))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
            .run { responseBodyContent?.isNotEmpty()?.let { assertTrue(it) } }

        accountRepository.findOneByLogin(defaultAccount.login!!).run {
            accountAuthorityRepository.deleteAllByAccountId(this?.id!!)
            accountRepository.delete(this.toAccount())
        }
        assertEquals(0, accountAuthorityRepository.count())
        assertEquals(0, accountRepository.count())
    }

    @Test
    fun `test register account avec un email dupliqué`(): Unit = runBlocking {

        assertEquals(0, accountRepository.count())
        assertEquals(0, accountAuthorityRepository.count())
        // First user
        // Register first user
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(defaultAccount)
            .exchange()
            .expectStatus()
            .isCreated
            .returnResult<Unit>()
            .run { responseBodyContent?.isEmpty()?.let { assertTrue(it) } }
        assertEquals(1, accountRepository.count())
        assertEquals(1, accountAuthorityRepository.count())
        assertFalse(accountRepository.findOneByEmail(defaultAccount.email!!)!!.activated)

        // Duplicate email, different login
        // Register second (non activated) user
        val secondLogin = "foo"
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(defaultAccount.copy(login = secondLogin))
            .exchange()
            .expectStatus()
            .isCreated
            .returnResult<Unit>()
            .run { responseBodyContent?.isEmpty()?.let { assertTrue(it) } }
        assertEquals(1, accountRepository.count())
        assertEquals(1, accountAuthorityRepository.count())
        assertNull(accountRepository.findOneByLogin(defaultAccount.login!!))
        accountRepository.findOneByLogin(secondLogin).run {
            assertNotNull(this)
            assertEquals(defaultAccount.email!!, email)
        }
        assertFalse(accountRepository.findOneByEmail(defaultAccount.email!!)!!.activated)


        // Duplicate email - with uppercase email address
        // Register third (not activated) user
        val thirdLogin = "bar"
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                defaultAccount.copy(
                    login = thirdLogin,
                    email = defaultAccount.email!!.uppercase()
                )
            )
            .exchange()
            .expectStatus()
            .isCreated
            .returnResult<Unit>()
            .run { responseBodyContent?.isEmpty()?.let { assertTrue(it) } }
        assertEquals(1, accountRepository.count())
        assertEquals(1, accountAuthorityRepository.count())
        assertNull(accountRepository.findOneByLogin(secondLogin))
        accountRepository.findOneByLogin(thirdLogin).run {
            assertNotNull(this)
            assertEquals(defaultAccount.email!!.uppercase(), email)
        }
        //activate third
        accountRepository.save(accountRepository.findOneByEmail(defaultAccount.email!!)!!.copy(activated = true))


        log.info(
            "accountRepository.findOneByEmail(defaultAccount.email!!): ${
                accountRepository.findOneByEmail(defaultAccount.email!!)
            }"
        )
//        assertTrue(accountRepository.findOneByEmail(defaultAccount.email!!)!!.activated)
//
//        // Register 4th (already activated) user
//        client
//            .post()
//            .uri(SIGNUP_URI)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(defaultAccount.copy(login = secondLogin))
//            .exchange()
//            .expectStatus()
//            .isBadRequest
//            .returnResult<Unit>()
//            .run { responseBodyContent?.isEmpty()?.let { assertTrue(it) } }
        assertEquals(1, accountRepository.count())
        assertEquals(1, accountAuthorityRepository.count())
//        assertNull(accountRepository.findOneByLogin(secondLogin))
//        accountRepository.findOneByLogin(thirdLogin).run {
//            assertNotNull(this)
////            assertEquals(defaultAccount.email!!.lowercase(), email!!.lowercase())
//        }
//        assertTrue(accountRepository.findOneByEmail(defaultAccount.email!!)!!.activated)

        //netoyage des accounts et accountAuthorities à la fin du test
        accountRepository.findOneByEmail(defaultAccount.email!!).run {
            accountAuthorityRepository.deleteAllByAccountId(this!!.id!!)
            accountRepository.delete(this.toAccount())
        }
        assertEquals(0, accountRepository.count())
        assertEquals(0, accountAuthorityRepository.count())

        /*
            // First user
            ManagedUserVM firstUser = new ManagedUserVM();
            firstUser.setLogin("test-register-duplicate-email");
            firstUser.setPassword("password");
            firstUser.setFirstName("Alice");
            firstUser.setLastName("Test");
            firstUser.setEmail("test-register-duplicate-email@example.com");
            firstUser.setImageUrl("http://placehold.it/50x50");
            firstUser.setLangKey(Constants.DEFAULT_LANGUAGE);
            firstUser.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

            // Register first user
            accountWebTestClient
                .post()
                .uri("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(TestUtil.convertObjectToJsonBytes(firstUser))
                .exchange()
                .expectStatus()
                .isCreated();

            Optional<User> testUser1 = userRepository.findOneByLogin("test-register-duplicate-email").blockOptional();
            assertThat(testUser1).isPresent();

            // Duplicate email, different login
            ManagedUserVM secondUser = new ManagedUserVM();
            secondUser.setLogin("test-register-duplicate-email-2");
            secondUser.setPassword(firstUser.getPassword());
            secondUser.setFirstName(firstUser.getFirstName());
            secondUser.setLastName(firstUser.getLastName());
            secondUser.setEmail(firstUser.getEmail());
            secondUser.setImageUrl(firstUser.getImageUrl());
            secondUser.setLangKey(firstUser.getLangKey());
            secondUser.setAuthorities(new HashSet<>(firstUser.getAuthorities()));

            // Register second (non activated) user
            accountWebTestClient
                .post()
                .uri("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(TestUtil.convertObjectToJsonBytes(secondUser))
                .exchange()
                .expectStatus()
                .isCreated();

            Optional<User> testUser2 = userRepository.findOneByLogin("test-register-duplicate-email").blockOptional();
            assertThat(testUser2).isEmpty();

            Optional<User> testUser3 = userRepository.findOneByLogin("test-register-duplicate-email-2").blockOptional();
            assertThat(testUser3).isPresent();

            // Duplicate email - with uppercase email address
            ManagedUserVM userWithUpperCaseEmail = new ManagedUserVM();
            userWithUpperCaseEmail.setId(firstUser.getId());
            userWithUpperCaseEmail.setLogin("test-register-duplicate-email-3");
            userWithUpperCaseEmail.setPassword(firstUser.getPassword());
            userWithUpperCaseEmail.setFirstName(firstUser.getFirstName());
            userWithUpperCaseEmail.setLastName(firstUser.getLastName());
            userWithUpperCaseEmail.setEmail("TEST-register-duplicate-email@example.com");
            userWithUpperCaseEmail.setImageUrl(firstUser.getImageUrl());
            userWithUpperCaseEmail.setLangKey(firstUser.getLangKey());
            userWithUpperCaseEmail.setAuthorities(new HashSet<>(firstUser.getAuthorities()));

            // Register third (not activated) user
            accountWebTestClient
                .post()
                .uri("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(TestUtil.convertObjectToJsonBytes(userWithUpperCaseEmail))
                .exchange()
                .expectStatus()
                .isCreated();

            Optional<User> testUser4 = userRepository.findOneByLogin("test-register-duplicate-email-3").blockOptional();
            assertThat(testUser4).isPresent();
            assertThat(testUser4.get().getEmail()).isEqualTo("test-register-duplicate-email@example.com");

            testUser4.get().setActivated(true);
            userService.updateUser((new AdminUserDTO(testUser4.get()))).block();

            // Register 4th (already activated) user
            accountWebTestClient
                .post()
                .uri("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(TestUtil.convertObjectToJsonBytes(secondUser))
                .exchange()
                .expectStatus()
                .is4xxClientError();

     */
    }

//    /*
//        @Test//test en renseignant l'authorité admin est ignoré
//        void testRegisterAdminIsIgnored() throws Exception {
//            ManagedUserVM validUser = new ManagedUserVM();
//            validUser.setLogin("badguy");
//            validUser.setPassword("password");
//            validUser.setFirstName("Bad");
//            validUser.setLastName("Guy");
//            validUser.setEmail("badguy@example.com");
//            validUser.setActivated(true);
//            validUser.setImageUrl("http://placehold.it/50x50");
//            validUser.setLangKey(Constants.DEFAULT_LANGUAGE);
//            validUser.setAuthorities(Collections.singleton(AuthoritiesConstants.ADMIN));
//
//            accountWebTestClient
//                .post()
//                .uri("/api/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(TestUtil.convertObjectToJsonBytes(validUser))
//                .exchange()
//                .expectStatus()
//                .isCreated();
//
//            Optional<User> userDup = userRepository.findOneWithAuthoritiesByLogin("badguy").blockOptional();
//            assertThat(userDup).isPresent();
//            assertThat(userDup.get().getAuthorities())
//                .hasSize(1)
//                .containsExactly(authorityRepository.findById(AuthoritiesConstants.USER).block());
//        }
//    */
//

}