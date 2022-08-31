@file:Suppress(
    "NonAsciiCharacters", "unused"
)

package backend

import backend.Constants.DEFAULT_LANGUAGE
import backend.Constants.SYSTEM_USER
import backend.Data.USER_LOGIN
import backend.Data.defaultAccount
import backend.Data.defaultUser
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import javax.validation.ValidationException
import kotlin.test.Test
import kotlin.test.assertEquals


internal class SignUpAccountControllerFunctionalTest {

    private lateinit var context: ConfigurableApplicationContext
    private val accountRepository: IAccountModelRepository by lazy { context.getBean() }
    private val accountAuthorityRepository: IAccountAuthorityRepository by lazy { context.getBean() }
    private val client: WebTestClient by lazy {
        WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:8080")
            .build()
    }


    @BeforeAll
    fun `lance le server en profile test`() =
        runApplication<Server> { testLoader(app = this) }
            .run { context = this }


    @AfterAll
    fun `arrête le serveur`() = context.close()

    @Test
    fun `signup avec un account valide`(): Unit = runBlocking {
        val countUserBefore = accountRepository.count()
        val countUserAuthBefore = accountAuthorityRepository.count()
        assertEquals(0, countUserBefore)
        assertEquals(0, countUserAuthBefore)
        client
            .post()
            .uri("/api/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(defaultAccount)
            .exchange()
            .returnResult<Unit>().run {
                assert(requestBodyContent!!.isNotEmpty())
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
                                "\"imageUrl\":\"${imageUrl}\""
                            ).map { assert(this@requestContent?.contains(it) ?: false) }
                        }
                    }
                responseBodyContent?.isEmpty()?.let { assert(it) }
                assertEquals(expected = HttpStatus.CREATED, actual = status)
            }
        assertEquals(countUserBefore + 1, accountRepository.count())
        assertEquals(countUserAuthBefore + 1, accountAuthorityRepository.count())
        //clean after test
        accountRepository.findOneByLogin(defaultAccount.login!!).run {
            accountAuthorityRepository.deleteAllByAccountId(this?.id!!)
            accountRepository.delete(this)
        }

        assertEquals(countUserAuthBefore, accountAuthorityRepository.count())
        assertEquals(countUserBefore, accountRepository.count())
    }

    //TODO: register un user avec un email invalid
    //TODO: register un user avec un email existant
    //TODO: register un user avec un mauvais login
    //TODO: register un user avec un login existant
    //TODO: mocker que l'email est parti en interceptant l'appel et logger l'action(en affichant le mail)


    @Test
    fun `test register account avec login invalid`(): Unit = runBlocking {
        assertEquals(0, accountRepository.count())
         client
            .post()
            .uri("/api/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(defaultUser.copy(login = "funky-log(n"))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
        assertEquals(accountRepository.count(), 0)
    }


//
//    @Test
//    @Throws(Exception::class)
//    fun `test register account avec un email invalid`(): Unit = runBlocking {
//        assertEquals(countUser(), 0)
//        client
//            .post()
//            .uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(
//                AccountPassword(
//                    password = defaultAccount.password
//                ).apply {
//                    login = defaultAccount.login
//                    firstName = USER_LOGIN
//                    lastName = USER_LOGIN
//                    email = "invalid"
//                    langKey = DEFAULT_LANGUAGE
//                    createdBy = SYSTEM_USER
//                    createdDate = Instant.now()
//                    lastModifiedBy = SYSTEM_USER
//                    lastModifiedDate = Instant.now()
//                    imageUrl = "http://placehold.it/50x50"
//                })
//            .exchange()
//            .expectStatus()
//            .isBadRequest
//        assertEquals(expected = countUser(), actual = 0)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun `test register account avec un password invalid`(): Unit = runBlocking {
//        assertEquals(expected = countUser(), actual = 0)
//        client.post()
//            .uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(
//                AccountPassword(
//                    password = "123"
//                ).apply {
//                    login = defaultAccount.login
//                    firstName = USER_LOGIN
//                    lastName = USER_LOGIN
//                    email = defaultAccount.email
//                    langKey = DEFAULT_LANGUAGE
//                    createdBy = SYSTEM_USER
//                    createdDate = Instant.now()
//                    lastModifiedBy = SYSTEM_USER
//                    lastModifiedDate = Instant.now()
//                    imageUrl = "http://placehold.it/50x50"
//                }
//            )
//            .exchange()
//            .expectStatus()
//            .isBadRequest
//        assertEquals(countUser(), 0)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun `test register account avec un password null`(): Unit = runBlocking {
//        assertEquals(countUser(), 0)
//        client
//            .post()
//            .uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(
//                AccountPassword(
//                    password = null
//                ).apply {
//                    login = defaultAccount.login
//                    firstName = USER_LOGIN
//                    lastName = USER_LOGIN
//                    email = defaultAccount.email
//                    langKey = DEFAULT_LANGUAGE
//                    createdBy = SYSTEM_USER
//                    createdDate = Instant.now()
//                    lastModifiedBy = SYSTEM_USER
//                    lastModifiedDate = Instant.now()
//                    imageUrl = "http://placehold.it/50x50"
//                })
//            .exchange()
//            .expectStatus()
//            .isBadRequest
//        assertEquals(countUser(), 0)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun `test register account avec un email existant activé`(): Unit = runBlocking {
//        assertEquals(0, countUser())
//        assertEquals(0, countUserAuthority())
//        checkInitDatabaseWithDefaultUser()
//        assertEquals(1, countUser())
//        assertEquals(1, countUserAuthority())
//
//        client
//            .post()
//            .uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(
//                AccountPassword(
//                    password = defaultAccount.password
//                ).apply {
//                    login = TEST_USER_LOGIN
//                    firstName = defaultAccount.firstName
//                    lastName = defaultAccount.lastName
//                    email = defaultAccount.email
//                    langKey = defaultAccount.langKey
//                    createdBy = defaultAccount.createdBy
//                    Instant.now().apply {
//                        lastModifiedDate = this
//                        lastModifiedDate = this
//                    }
//                    lastModifiedBy = defaultAccount.lastModifiedBy
//                    imageUrl = "http://placehold.it/50x50"
//                })
//            .exchange()
//            .expectStatus()
//            .is4xxClientError
//
//        assertEquals(1, countUser())
//    }
//
//
//    @Test
//    @Throws(Exception::class)
//    fun `test register account avec un login existant`(): Unit = runBlocking {
//        assertEquals(0, countUser())
//        assertEquals(0, countUserAuthority())
//        checkInitDatabaseWithDefaultUser()
//        assertEquals(1, countUser())
//        assertEquals(1, countUserAuthority())
//
//        client
//            .post()
//            .uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(
//                AccountPassword(
//                    password = defaultAccount.password
//                ).apply {
//                    login = defaultAccount.login
//                    firstName = defaultAccount.firstName
//                    lastName = defaultAccount.lastName
//                    email = "j.doe@acme.com"
//                    langKey = defaultAccount.langKey
//                    createdBy = defaultAccount.createdBy
//                    createdDate = Instant.now()
//                    lastModifiedBy = defaultAccount.lastModifiedBy
//                    lastModifiedDate = Instant.now()
//                })
//            .exchange()
//            .expectStatus()
//            .isBadRequest
//
//        assertEquals(1, countUser())
//    }
//
//    @Ignore
//    @Test
//    @Throws(Exception::class)
//    fun `test register account avec un email dupliqué`(): Unit = runBlocking {
///*
//        // First user
//        ManagedUserVM firstUser = new ManagedUserVM();
//        firstUser.setLogin("test-register-duplicate-email");
//        firstUser.setPassword("password");
//        firstUser.setFirstName("Alice");
//        firstUser.setLastName("Test");
//        firstUser.setEmail("test-register-duplicate-email@example.com");
//        firstUser.setImageUrl("http://placehold.it/50x50");
//        firstUser.setLangKey(Constants.DEFAULT_LANGUAGE);
//        firstUser.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));
//
//        // Register first user
//        accountWebTestClient
//            .post()
//            .uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(TestUtil.convertObjectToJsonBytes(firstUser))
//            .exchange()
//            .expectStatus()
//            .isCreated();
//
//        Optional<User> testUser1 = userRepository.findOneByLogin("test-register-duplicate-email").blockOptional();
//        assertThat(testUser1).isPresent();
//
//        // Duplicate email, different login
//        ManagedUserVM secondUser = new ManagedUserVM();
//        secondUser.setLogin("test-register-duplicate-email-2");
//        secondUser.setPassword(firstUser.getPassword());
//        secondUser.setFirstName(firstUser.getFirstName());
//        secondUser.setLastName(firstUser.getLastName());
//        secondUser.setEmail(firstUser.getEmail());
//        secondUser.setImageUrl(firstUser.getImageUrl());
//        secondUser.setLangKey(firstUser.getLangKey());
//        secondUser.setAuthorities(new HashSet<>(firstUser.getAuthorities()));
//
//        // Register second (non activated) user
//        accountWebTestClient
//            .post()
//            .uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(TestUtil.convertObjectToJsonBytes(secondUser))
//            .exchange()
//            .expectStatus()
//            .isCreated();
//
//        Optional<User> testUser2 = userRepository.findOneByLogin("test-register-duplicate-email").blockOptional();
//        assertThat(testUser2).isEmpty();
//
//        Optional<User> testUser3 = userRepository.findOneByLogin("test-register-duplicate-email-2").blockOptional();
//        assertThat(testUser3).isPresent();
//
//        // Duplicate email - with uppercase email address
//        ManagedUserVM userWithUpperCaseEmail = new ManagedUserVM();
//        userWithUpperCaseEmail.setId(firstUser.getId());
//        userWithUpperCaseEmail.setLogin("test-register-duplicate-email-3");
//        userWithUpperCaseEmail.setPassword(firstUser.getPassword());
//        userWithUpperCaseEmail.setFirstName(firstUser.getFirstName());
//        userWithUpperCaseEmail.setLastName(firstUser.getLastName());
//        userWithUpperCaseEmail.setEmail("TEST-register-duplicate-email@example.com");
//        userWithUpperCaseEmail.setImageUrl(firstUser.getImageUrl());
//        userWithUpperCaseEmail.setLangKey(firstUser.getLangKey());
//        userWithUpperCaseEmail.setAuthorities(new HashSet<>(firstUser.getAuthorities()));
//
//        // Register third (not activated) user
//        accountWebTestClient
//            .post()
//            .uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(TestUtil.convertObjectToJsonBytes(userWithUpperCaseEmail))
//            .exchange()
//            .expectStatus()
//            .isCreated();
//
//        Optional<User> testUser4 = userRepository.findOneByLogin("test-register-duplicate-email-3").blockOptional();
//        assertThat(testUser4).isPresent();
//        assertThat(testUser4.get().getEmail()).isEqualTo("test-register-duplicate-email@example.com");
//
//        testUser4.get().setActivated(true);
//        userService.updateUser((new AdminUserDTO(testUser4.get()))).block();
//
//        // Register 4th (already activated) user
//        accountWebTestClient
//            .post()
//            .uri("/api/register")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(TestUtil.convertObjectToJsonBytes(secondUser))
//            .exchange()
//            .expectStatus()
//            .is4xxClientError();
//
// */
//    }
//
//    /*
//        @Test
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