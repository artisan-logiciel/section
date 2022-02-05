package backend.features

//import io.cucumber.java8.En
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import backend.calculator.add
import backend.calculator.subtract


//class CalculatorStepDefinition: En {
//    init{
//     var firstNumber: Int = 0
//     var secondNumber: Int = 0
//     var result: Int = 0
//    }
//}


class CalculatorStepDefinition {
    private var firstNumber: Int = 0
    private var secondNumber: Int = 0
    private var result: Int = 0

    @Before
    fun setUp() {
        firstNumber = 0
        secondNumber = 0
        result = 0
    }

    @Given(value = "a integer {int}")
    fun `a integer`(number: Int) {
        firstNumber = number
    }

    @And(value = "a second integer {int}")
    fun `a second integer`(number: Int) {
        secondNumber = number
    }

    @When(value = "the numbers are added")
    fun `the numbers are added`() {
        result = add(firstNumber, secondNumber)
    }

    @When(value = "the numbers are subtracted")
    fun `the numbers are subtracted`() {
        result = subtract(firstNumber, secondNumber)
    }

    @Then(value = "the result is {int}")
    fun `the result is`(result: Int) {
        assert(result == this.result)
    }

}