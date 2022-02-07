package backend.features

import backend.calculator.add
import backend.calculator.subtract
import io.cucumber.java8.En

class CalculatorStepDefinition : En {
    private var firstNumber: Int = 0
    private var secondNumber: Int = 0
    private var result: Int = 0


    init {
        Before { _ ->
            firstNumber = 0
            secondNumber = 0
            result = 0
        }
        Given("a integer {int}") { number: Int ->
            firstNumber = number
        }

        And("a second integer {int}") { number: Int ->
            secondNumber = number
        }

        When("the numbers are added") {
            result = add(firstNumber, secondNumber)
        }

        When("the numbers are subtracted") {
            result = subtract(firstNumber, secondNumber)
        }

        Then("the result is {int}") { result: Int ->
            assert(result == this.result)
        }
    }
}