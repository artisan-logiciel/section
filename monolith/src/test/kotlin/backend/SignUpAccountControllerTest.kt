@file:Suppress(
    "NonAsciiCharacters", "unused", "GrazieInspection"
)

package backend

import backend.Constants.ROLE_USER
import backend.Data.defaultAccount
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import kotlin.test.*


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
    fun `signup avec un account valide`() {
        val countUserBefore = countAccount(dao)
        val countUserAuthBefore = countAccountAuthority(dao)
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
        assertEquals(countUserBefore + 1, countAccount(dao))
        assertEquals(countUserAuthBefore + 1, countAccountAuthority(dao))
        assertFalse(findOneByEmail(defaultAccount.email!!, dao)!!.activated)
    }


    @Test
    fun `test register account avec login invalid`() {
        assertEquals(0, countAccount(dao))
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(defaultAccount.copy(login = "funky-log(n"))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
            .run { responseBodyContent?.isNotEmpty()?.let { assertTrue(it) } }
        assertEquals(0, countAccount(dao))
    }


    @Test
    fun `test register account avec un email invalid`() {
        val countBefore = countAccount(dao)
        assertEquals(0, countBefore)
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
        assertEquals(0, countBefore)
    }

    @Test
    fun `test register account avec un password invalid`() {
        assertEquals(0, countAccount(dao))
        client.post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(defaultAccount.copy(password = "123"))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
            .run { responseBodyContent?.isNotEmpty()?.let { assertTrue(it) } }
        assertEquals(0, countAccount(dao))
    }

    @Test
    fun `test register account avec un password null`() {
        assertEquals(0, countAccount(dao))
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(defaultAccount.copy(password = null))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
            .run {
                responseBodyContent?.isNotEmpty()?.let { assertTrue(it) }


            }
        assertEquals(0, countAccount(dao))
    }

    @Test
    fun `test register account activé avec un email existant`() {
        assertEquals(0, countAccount(dao))
        assertEquals(0, countAccountAuthority(dao))
        saveAccountAuthority(
            saveAccount(defaultAccount.copy(activated = true), dao)?.id!!,
            ROLE_USER, dao
        )
        assertEquals(1, countAccount(dao))
        assertEquals(1, countAccountAuthority(dao))
        assertTrue(findOneByEmail(defaultAccount.email!!, dao)!!.activated)

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

//        deleteAllAccountAuthority(dao)
//        deleteAccounts(dao)
//
//        assertEquals(0, countAccountAuthority(dao))
//        assertEquals(0, countAccount(dao))
    }


    @Test
    fun `test register account activé avec un login existant`() {
        assertEquals(0, countAccount(dao))
        assertEquals(0, countAccountAuthority(dao))
        saveAccountAuthority(
            saveAccount(defaultAccount.copy(activated = true), dao)?.id!!,
            ROLE_USER, dao
        )
        assertTrue(findOneByEmail(defaultAccount.email!!, dao)!!.activated)
        assertEquals(1, countAccount(dao))
        assertEquals(1, countAccountAuthority(dao))

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

//        deleteAllAccountAuthority(dao)
//        deleteAccounts(dao)
//
//
//        assertEquals(0, countAccountAuthority(dao))
//        assertEquals(0, countAccount(dao))
    }


    @Test
    @Ignore
    fun `test register account avec un email dupliqué`() {

        assertEquals(0, countAccount(dao))
        assertEquals(0, countAccountAuthority(dao))
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
        assertEquals(1, countAccount(dao))
        assertEquals(1, countAccountAuthority(dao))
        assertFalse(findOneByEmail(defaultAccount.email!!, dao)!!.activated)

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
        assertEquals(1, countAccount(dao))
        assertEquals(1, countAccountAuthority(dao))
        assertNull(findOneByLogin(defaultAccount.login!!, dao))
        findOneByLogin(secondLogin, dao).run {
            assertNotNull(this)
            assertEquals(defaultAccount.email!!, email)
        }
        assertFalse(findOneByEmail(defaultAccount.email!!, dao)!!.activated)


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
        assertEquals(1, countAccount(dao))
        assertEquals(1, countAccountAuthority(dao))
        assertNull(findOneByLogin(secondLogin, dao))
        findOneByLogin(thirdLogin, dao).run {
            assertNotNull(this)
            assertEquals(defaultAccount.email!!.uppercase(), email)
        }
        //activate third
        saveAccount(findOneByEmail(defaultAccount.email!!, dao)!!.copy(activated = true), dao)


//        log.info(
//            "findOneByEmail(defaultAccount.email!!): ${
//                findOneByEmail(defaultAccount.email!!, dao)
//            }"
//        )
//        assertTrue(findOneByEmail(defaultAccount.email!!)!!.activated)
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
        assertEquals(1, countAccount(dao))
        assertEquals(1, countAccountAuthority(dao))
//        assertNull(findOneByLogin(secondLogin))
//        findOneByLogin(thirdLogin).run {
//            assertNotNull(this)
////            assertEquals(defaultAccount.email!!.lowercase(), email!!.lowercase())
//        }
//        assertTrue(findOneByEmail(defaultAccount.email!!)!!.activated)


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
//        @Test @Ignore//test en renseignant l'authorité admin est ignoré
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

