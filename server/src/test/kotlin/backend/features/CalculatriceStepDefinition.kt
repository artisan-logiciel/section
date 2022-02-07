package backend.features

import backend.Server
import backend.calculator.add
import backend.calculator.subtract
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
import kotlin.test.assertEquals


@SpringBootTest(
    classes = [Server::class],
    webEnvironment = DEFINED_PORT
)
@ActiveProfiles("test")
@Suppress("unused")
class CalculatriceStepDefinition : En {
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    private lateinit var response: ClientResponse
    private val client = WebClient.builder()
        .baseUrl("http://localhost:8080/api/")
        .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .build()
    private var firstNumber: Int = 0
    private var secondNumber: Int = 0
    private var result: Int = 0

    init {
        Before { _ ->
            firstNumber = 0
            secondNumber = 0
            result = 0
        }
        After { _ -> }
        Given("un entier {int}") { number: Int ->
            firstNumber = number
        }
        And("un second entier {int}") { number: Int ->
            secondNumber = number
        }
        When("on additionne les nombres") {
            result = add(firstNumber, secondNumber)
        }
        When("on soustrait un nombre à l'autre") {
            result = subtract(firstNumber, secondNumber)
        }
        Then("le resultat est {int}") { expectedResult: Int ->
            assertEquals(
                expected = expectedResult,
                actual = result
            )
        }
    }
}
