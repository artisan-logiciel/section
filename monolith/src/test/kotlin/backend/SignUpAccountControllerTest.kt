@file:Suppress(
    "NonAsciiCharacters", "unused"
)

package backend

import backend.Datas.defaultAccount
import backend.tdd.testLoader
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import kotlin.test.Test
import kotlin.test.assertEquals


internal class SignUpAccountControllerTest {

    private lateinit var context: ConfigurableApplicationContext

    private val client: WebTestClient by lazy {
        WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:8080")
            .build()
    }

    @BeforeAll
    fun `lance le server en profile test`() =
        runApplication<Server> { testLoader(app = this) }.run { context = this }


    @AfterAll
    fun `arrête le serveur`() = context.close()

    @Test
    fun `signup account`(): Unit = runBlocking {
        //TODO: compter les user_auth comme avec user
        //        log.info("count 1 user authorities: ${context.getBean<UserAuthRepository>().count()}")
        //        log.info("count 2 user authorities: ${context.getBean<UserAuthRepository>().count()}")
        val countUserBefore = context.getBean<IAccountModelRepository>().count()
//        val countUserAuthBefore = context.getBean<UserAuthRepository>().count()
        assertEquals(0, countUserBefore)
//        assertEquals(0, countUserAuthBefore)
        client
            .post()
            .uri("/api/signup")
            .bodyValue(defaultAccount)
            .exchange()
            .returnResult<Unit>().apply {
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
        assertEquals(countUserBefore + 1, context.getBean<IAccountModelRepository>().count())
//        assertEquals(countUserAuthBefore + 1, context.getBean<UserAuthRepository>().count())
        //clean after test
        context.getBean<IAccountModelRepository>().run { delete(findOneByLogin(defaultAccount.login!!)!!) }
//        context.getBean<UserAuthRepository>().deleteAll()
//        context.getBean<UserRepository>().deleteAll()
//        assertEquals(countUserAuthBefore, context.getBean<UserAuthRepository>().count())
        assertEquals(countUserBefore, context.getBean<IAccountModelRepository>().count())
    }

    //TODO: register un user avec un email invalid
    //TODO: register un user avec un email existant
    //TODO: register un user avec un mauvais login
    //TODO: register un user avec un login existant
    //TODO: mocker que l'email est parti en interceptant l'appel et logger l'action(en affichant le mail)


}