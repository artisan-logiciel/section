@file:Suppress(
    "NonAsciiCharacters",
    "unused"
)

package backend.http

import backend.Server
import backend.Server.Log.log
import backend.domain.Account
import backend.test.Datas.defaultAccount
import backend.test.testLoader
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpStatus.CREATED
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import kotlin.test.Test
import kotlin.test.assertEquals

class RegistrationTest {

    lateinit var context: ConfigurableApplicationContext

    private val client: WebTestClient by lazy {
        WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:8080")
            .build()
    }

    @BeforeAll
    fun `lance le server en profile test`() {
        runApplication<Server> {
            testLoader(app = this)
        }.apply { context = this }
    }

    @AfterAll
    fun `arrête le serveur`() = context.close()

    @Test
    fun `register user`() {
        log.info("start register user test defaultAccount: $defaultAccount")
        client
            .post().uri("/api/register")
            .bodyValue(defaultAccount)
            .exchange()
            .returnResult<Account>()
            .apply {
                requestBodyContent!!
                    .map { it.toInt().toChar().toString() }
                    .reduce { acc: String, s: String -> acc + s }
                    .apply {
                        //test request contains passed values
                        defaultAccount.run {
                            setOf(
                                "\"login\":\"${login}\"",
                                "\"password\":\"${password}\"",
                                "\"firstName\":\"${firstName}\"",
                                "\"lastName\":\"${lastName}\"",
                                "\"email\":\"${email}\"",
                                "\"imageUrl\":\"${imageUrl}\""
                            ).map { assert(contains(it)) }
                        }
                    }

                log.info("request: ${
                    requestBodyContent!!
                        .map { it.toInt().toChar().toString() }
                        .reduce { acc: String, s: String -> acc + s }
                }")
                log.info("response: ${
                    responseBodyContent?.map { it.toInt().toChar().toString() }
                        ?.reduce { acc: String, s: String -> acc + s }
                }")

                assertEquals(expected = CREATED, actual = status)

                responseBodyContent!!.map { it.toInt().toChar().toString() }
                    .reduce { acc: String, s: String -> acc + s }.apply {
                        //test response contains right values
                        defaultAccount.run {
                            setOf(
//                                "\"login\":\"${login}\"",
//                                "\"password\":\"${password}\"",
//                                "\"firstName\":\"${firstName}\"",
//                                "\"lastName\":\"${lastName}\"",
//                                "\"email\":\"${email}\"",
                                "\"imageUrl\":\"${imageUrl}\""
                            ).map { assert(contains(it)) }
                        }
                    }
            }
    }
}