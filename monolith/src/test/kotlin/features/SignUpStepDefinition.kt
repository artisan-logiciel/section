@file:Suppress("LeakingThis", "unused")

package features

import backend.Server
import backend.Log.log
import backend.RandomUtils
import com.fasterxml.jackson.databind.ObjectMapper
import backend.Account
import backend.Account.AccountCredentials
import io.cucumber.datatable.DataTable
import io.cucumber.java8.Fr
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import reactor.kotlin.core.publisher.toMono
import kotlin.test.assertEquals

@SpringBootTest(
    classes = [Server::class],
    webEnvironment = DEFINED_PORT
)
@ActiveProfiles("test")
class SignUpStepDefinition : Fr {
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    //    private lateinit var response: org.springframework.web.reactive.function.client.ClientResponse
    private lateinit var response: WebTestClient.ResponseSpec
    private val client = WebTestClient.bindToServer()
        .baseUrl("http://localhost:8080")
        .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .build()
    private lateinit var currentAccount: AccountCredentials
    private var accounts: List<AccountCredentials> = mutableListOf()



    init {
//initialiser un coroutine context reactor

        Etantdonné("une liste de login, email, password, firstName, lastName") { dataTable: DataTable ->
            if (accounts.isNotEmpty()) (accounts as MutableList).clear()
            dataTable.asMaps().map {
                (accounts as MutableList).add(
                    AccountCredentials(
                        activationKey = RandomUtils.generateActivationKey
                    ).apply {
                        login = it["login"]
                        email = it["email"]
                        password = it["password"]
                        firstName = it["firstName"]
                        lastName = it["lastName"]
                        activated = false
                        authorities = mutableSetOf()
                    }
                )
            }
        }
        Etantdonné("l'utilisateur qui à pour login {string}") { login: String ->
            currentAccount = accounts.first { it.login.equals(login, ignoreCase = true) }
        }
        Quand("on envoie l'inscription de {string}") { login: String ->
            assertEquals(expected = currentAccount.login, actual = login)
            mono {
                client.post().uri("/api/register")
                    .bodyValue(currentAccount)
                    .toMono()
                    .block()
                    ?.exchange().apply { response = this!! }
                    ?.returnResult<Account>().apply { log.info(this!!.status) }
            }
        }

        Alors("le résultat est la création d'un nouveau compte non activé") {
//                response.expectStatus().isCreated
//            response.returnResult<Account>().status
//            log.info(response.status)
//            mono {
//                mono { log.info(response.awaitEntity<Account>().statusCode) }.block()
//            println("passé par ici")
//            log.info("passé par ici")
//            }
            //TODO: ne pas oublier de netoyer la base
        }
    }

}


