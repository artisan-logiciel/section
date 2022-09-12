@file:Suppress(
    "NonAsciiCharacters", "unused", "GrazieInspection"
)

package backend

import backend.Constants.DEFAULT_LANGUAGE
import backend.Constants.ROLE_ADMIN
import backend.Constants.SIGNUP_URI
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

    private lateinit var context: ConfigurableApplicationContext

    private val client: WebTestClient by lazy {
        WebTestClient
            .bindToServer()
            .baseUrl(BASE_URL_DEV)
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
            .bodyValue(Data.defaultAccount)
            .exchange()
            .returnResult<Unit>()
            .requestBodyContent!!
            .map { it.toInt().toChar().toString() }
            .reduce { acc: String, s: String -> acc + s }
            .run {
                Data.defaultAccount.run {
                    setOf(
                        "\"login\":\"${login}\"",
                        "\"password\":\"${password}\"",
                        "\"firstName\":\"${firstName}\"",
                        "\"lastName\":\"${lastName}\"",
                        "\"email\":\"${email}\"",
                    ).map {
                        //test request contient les parametres passés
                        assertTrue(contains(it))
                    }
                }
            }
    }

    @Test
    fun `test signup avec un account valide`() {
        val countUserBefore = countAccount(dao)
        val countUserAuthBefore = countAccountAuthority(dao)
        assertEquals(0, countUserBefore)
        assertEquals(0, countUserAuthBefore)
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Data.defaultAccount)
            .exchange()
            .expectStatus()
            .isCreated
            .returnResult<Unit>()
            .responseBodyContent!!.isEmpty().run { assertTrue(this) }
        assertEquals(countUserBefore + 1, countAccount(dao))
        assertEquals(countUserAuthBefore + 1, countAccountAuthority(dao))
        findOneByEmail(Data.defaultAccount.email!!, dao).run {
            assertNotNull(this)
            assertFalse(activated)
            assertNotNull(activationKey)
        }
    }


    @Test
    fun `test signup account avec login invalid`() {
        assertEquals(0, countAccount(dao))
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Data.defaultAccount.copy(login = "funky-log(n"))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
            .responseBodyContent!!.isNotEmpty().run { assertTrue(this) }
        assertEquals(0, countAccount(dao))
    }


    @Test
    fun `test signup account avec un email invalid`() {
        val countBefore = countAccount(dao)
        assertEquals(0, countBefore)
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Data.defaultAccount.copy(password = "inv"))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
            .responseBodyContent!!.isNotEmpty().run { assertTrue(this) }

        assertEquals(0, countBefore)
    }

    @Test
    fun `test signup account avec un password invalid`() {
        assertEquals(0, countAccount(dao))
        client.post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Data.defaultAccount.copy(password = "123"))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
            .responseBodyContent!!.isNotEmpty().run { assertTrue(this) }

        assertEquals(0, countAccount(dao))
    }

    @Test
    fun `test signup account avec un password null`() {
        assertEquals(0, countAccount(dao))
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Data.defaultAccount.copy(password = null))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
            .responseBodyContent!!.isNotEmpty().run { assertTrue(this) }

        assertEquals(0, countAccount(dao))
    }

    @Test
    fun `test signup account activé avec un email existant`() {
        assertEquals(0, countAccount(dao))
        assertEquals(0, countAccountAuthority(dao))
        //activation de l'account
        createActivatedDataAccounts(setOf(Data.defaultAccount), dao)
        assertEquals(1, countAccount(dao))
        assertEquals(1, countAccountAuthority(dao))
        findOneByEmail(Data.defaultAccount.email!!, dao).run {
            assertNotNull(this)
            assertTrue(activated)
            assertNull(activationKey)
        }

        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Data.defaultAccount.copy(login = "foo"))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
            .responseBodyContent!!.isNotEmpty().run { assertTrue(this) }
    }


    @Test
    fun `test signup account activé avec un login existant`() {
        assertEquals(0, countAccount(dao))
        assertEquals(0, countAccountAuthority(dao))
        //activation de l'account
        createActivatedDataAccounts(setOf(Data.defaultAccount), dao)
        findOneByEmail(Data.defaultAccount.email!!, dao).run {
            assertNotNull(this)
            assertTrue(activated)
            assertNull(activationKey)
        }
        assertEquals(1, countAccount(dao))
        assertEquals(1, countAccountAuthority(dao))

        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Data.defaultAccount.copy(email = "foo@localhost"))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
            .responseBodyContent!!.isNotEmpty().run { assertTrue(this) }
    }

    @Test
    fun `test signup account avec un email dupliqué`() {

        assertEquals(0, countAccount(dao))
        assertEquals(0, countAccountAuthority(dao))
        // premier user
        // sign up premier user
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Data.defaultAccount)
            .exchange()
            .expectStatus()
            .isCreated
            .returnResult<Unit>()
            .responseBodyContent!!.isEmpty().run { assertTrue(this) }
        assertEquals(1, countAccount(dao))
        assertEquals(1, countAccountAuthority(dao))
        assertFalse(findOneByEmail(Data.defaultAccount.email!!, dao)!!.activated)

        // email dupliqué, login different
        // sign up un second user (non activé)
        val secondLogin = "foo"
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Data.defaultAccount.copy(login = secondLogin))
            .exchange()
            .expectStatus()
            .isCreated
            .returnResult<Unit>()
            .responseBodyContent!!.isEmpty().run { assertTrue(this) }
        assertEquals(1, countAccount(dao))
        assertEquals(1, countAccountAuthority(dao))
        assertNull(findOneByLogin(Data.defaultAccount.login!!, dao))
        findOneByLogin(secondLogin, dao).run {
            assertNotNull(this)
            assertEquals(Data.defaultAccount.email!!, email)
            assertFalse(activated)
        }

        // email dupliqué - avec un email en majuscule, login différent
        // sign up un troisieme user (non activé)
        val thirdLogin = "bar"
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                Data.defaultAccount.copy(
                    login = thirdLogin,
                    email = Data.defaultAccount.email!!.uppercase()
                )
            )
            .exchange()
            .expectStatus()
            .isCreated
            .returnResult<Unit>()
            .responseBodyContent!!.isEmpty().run { assertTrue(this) }
        assertEquals(1, countAccount(dao))
        assertEquals(1, countAccountAuthority(dao))
        findOneByLogin(thirdLogin, dao).run {
            assertNotNull(this)
            assertEquals(Data.defaultAccount.email!!, email!!.lowercase())
            assertFalse(activated)
            //activation du troisieme user
            saveAccount(copy(activated = true, activationKey = null), dao)
        }
        //validation que le troisieme est actif et activationKey est null
        findOneByLogin(thirdLogin, dao).run {
            assertNotNull(this)
            assertTrue(activated)
            assertNull(activationKey)
        }
        val fourthLogin = "baz"
        // sign up un quatrieme user avec login different et meme email
        // le user existant au meme mail est deja activé
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Data.defaultAccount.copy(login = fourthLogin))
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<Unit>()
            .responseBodyContent!!.isNotEmpty().run { assertTrue(this) }
        assertEquals(1, countAccount(dao))
        assertEquals(1, countAccountAuthority(dao))
        assertNull(findOneByLogin(fourthLogin, dao))
        //meme id
        assertEquals(
            findOneByLogin(thirdLogin, dao).apply {
                assertNotNull(this)
                assertTrue(activated)
                assertNull(activationKey)
                assertTrue(Data.defaultAccount.email!!.equals(email!!, ignoreCase = true))
            }!!.id,
            findOneByEmail(Data.defaultAccount.email!!, dao).apply {
                assertNotNull(this)
                assertTrue(activated)
                assertNull(activationKey)
                assertTrue(thirdLogin.equals(login, ignoreCase = true))
            }!!.id
        )
    }

    @Test
    fun `test signup account en renseignant l'authorité admin qui sera ignoré et activé qui sera mis à false`() {
        val countUserBefore = countAccount(dao)
        val countUserAuthBefore = countAccountAuthority(dao)
        assertEquals(0, countUserBefore)
        assertEquals(0, countUserAuthBefore)
        val login = "badguy"
        client
            .post()
            .uri(SIGNUP_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                AccountCredentials(
                    login = login,
                    password = "password",
                    firstName = "Bad",
                    lastName = "Guy",
                    email = "badguy@example.com",
                    activated = true,
                    imageUrl = "http://placehold.it/50x50",
                    langKey = DEFAULT_LANGUAGE,
                    authorities = setOf(ROLE_ADMIN),
                )
            )
            .exchange()
            .expectStatus()
            .isCreated
            .returnResult<Unit>()
            .responseBodyContent!!.isEmpty().run  { assertTrue(this) }
        assertEquals(countUserBefore + 1, countAccount(dao))
        assertEquals(countUserAuthBefore + 1, countAccountAuthority(dao))
        findOneByLogin(login, dao).run {
            assertNotNull(this)
            assertFalse(activated)
            assertFalse(activationKey.isNullOrBlank())
        }

        assertTrue(findAllAccountAuthority(dao).none {
            it.role.equals(ROLE_ADMIN, ignoreCase = true)
        })
    }
}