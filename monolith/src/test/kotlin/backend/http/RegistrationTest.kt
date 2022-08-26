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
//                assertEquals(
//                    expected = CREATED,
//                    actual = status
//                )
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
//                log.info(
                    requestBodyContent!!.map { it.toInt().toChar().toString() }
                    .fold("") { acc: String, c: String ->
                        return@fold acc=acc+c
                    }
//                )
                log.info(requestBodyContent!!.map { it.toInt().toChar().toString() })

                //{"password":"user","activationKey":null,"id":null,"login":"user","firstName":"user","lastName":"user","email":"user@acme.com","imageUrl":"http://placehold.it/50x50","activated":false,"langKey":"en","createdBy":"system","createdDate":1661529098.935499224,"lastModifiedBy":"system","lastModifiedDate":1661529098.935510288,"authorities":null}
            }
    }
}