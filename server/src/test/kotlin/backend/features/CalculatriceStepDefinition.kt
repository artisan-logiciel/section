package backend.features

import backend.Server
import com.fasterxml.jackson.databind.ObjectMapper
import io.cucumber.java8.En
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient


@ActiveProfiles("test")
@SpringBootTest(
    classes = [Server::class],
    webEnvironment = DEFINED_PORT
)
@Suppress("unused")
class CalculatriceStepDefinition : En {
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    private lateinit var response: ClientResponse
    private val client = WebClient.builder()
        .baseUrl("http://localhost:8080/api/")
        .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .build()

    init {
        Before { _ ->
        }
        After { _ ->

        }
        Given("un entier {int}") { int: Int -> }
        And("un second entier {int}") { number: Int -> }
        When("on additionne les nombres") {}
        When("on soustrait un nombre à l'autre") {}
        Then("le resultat est {int}") { result: Int -> }
    }
}
