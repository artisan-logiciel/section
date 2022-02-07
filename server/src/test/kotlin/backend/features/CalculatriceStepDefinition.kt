package backend.features
//https://github.com/walter-the-coder/spring-boot-cucumber-wiremock-simulator
import io.cucumber.java.Before
import io.cucumber.java.en.Then
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.When

class CalculatriceStepDefinition {

    @Before
    fun setUp() {
    }

    @Given("un entier {int}")
    fun `a integer`(number: Int) {
    }

    @And("un second entier {int}")
    fun `a second integer`(number: Int) {
    }

    @When("on additionne les nombres")
    fun `the numbers are added`() {

    }

    @When("on soustrait un nombre à l'autre")
    fun `the numbers are subtracted`() {
    }

    @Then("le resultat est {int}")
    fun `the result is`(result: Int) {
    }

}
