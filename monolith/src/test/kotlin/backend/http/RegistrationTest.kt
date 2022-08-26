package backend.http

import backend.Server
import backend.Server.Log.log
import backend.test.Datas.defaultAccount
import backend.test.testLoader
import backend.domain.Account
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpStatus.CREATED
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import java.nio.charset.Charset
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
    @Suppress("unused")
    fun `lance le server en profile test`() {
        runApplication<Server> {
            testLoader(app = this)
        }.apply { context = this }
    }

    @AfterAll
    @Suppress("unused")
    fun `arrête le serveur`() = context.close()

    @Test
    fun `register user`() {
        client
            .post().uri("/api/register")
            .bodyValue(defaultAccount)
            .exchange()
            .returnResult<Account>()
            .apply {
                assertEquals(
                    expected = CREATED,
                    actual = status
                )
//                responseBody.blockFirst().apply {
//                    assertEquals(
//                        expected = defaultAccount.login,
//                        actual = this?.login
//                    )
//                    assertEquals(
//                        expected = defaultAccount.email,
//                        actual = this?.email
//                    )
//                }
                log.info(
                    "request: ${
                        requestBodyContent!!
                            .map { it.toInt().toChar().toString() }
                            .reduce { acc: String, s: String -> acc + s }
                            .apply {
                                defaultAccount.run {
                                    assert(contains("\"login\":\"${login}\""))
                                    assert(contains("\"password\":\"${password}\""))
                                    assert(contains("\"firstName\":\"${firstName}\""))
                                    assert(contains("\"lastName\":\"${lastName}\""))
                                    assert(contains("\"email\":\"${email}\""))
                                    assert(contains("\"imageUrl\":\"${imageUrl}\""))
                                }
                            }
                    }"
                )
            }
    }
}