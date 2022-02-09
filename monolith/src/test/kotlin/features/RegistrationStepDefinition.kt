package features

import backend.Server
import com.fasterxml.jackson.databind.ObjectMapper
import common.domain.Account
import io.cucumber.datatable.DataTable
import io.cucumber.java8.Fr
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import kotlin.test.assertEquals

@SpringBootTest(
    classes = [Server::class],
    webEnvironment = DEFINED_PORT
)
@ActiveProfiles("test")
@Suppress("unused")
class RegistrationStepDefinition : Fr {
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    private lateinit var response: ClientResponse
    private val client = WebClient.builder()
        .baseUrl("http://localhost:8080/api/")
        .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .build()
    private lateinit var currentAccount: Account
    private var accounts: List<Account> = mutableListOf()

    init {
        Etantdonné("une liste d'accounts") { dataTable: DataTable ->
            if (accounts.isNotEmpty()) (accounts as MutableList).clear()
            dataTable.asMaps().map {
                (accounts as MutableList).add(
                    Account(
                        login = it["login"],
                        email = it["email"],
                        firstName = it["firstName"],
                        lastName = it["lastName"],
                        activated = false,
                        authorities = mutableSetOf(),
                    )
                )
            }
        }
        Etantdonné("un utilisateur qui a pour login {string}") { login: String ->
            currentAccount = accounts.first { it.login.equals(login, ignoreCase = true) }
        }
        Quand("on inscrit {string}") { login: String ->
            assertEquals(expected = currentAccount.login, actual = login)
        }
        Alors("le resultat est un nouveau compte") {
        }
    }
}