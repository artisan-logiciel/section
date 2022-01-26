package backend.features
//https://github.com/walter-the-coder/spring-boot-cucumber-wiremock-simulator
//import backend.calculator.add
//import backend.calculator.subtract
//import io.cucumber.java.Before
////import io.cucumber.java8.En
//import io.cucumber.java.en.And
//import io.cucumber.java.en.Given
//import io.cucumber.java.en.Then
//import io.cucumber.java.en.When
//import io.cucumber.java8.En
//
//
//
//class Calculator8StepDefinition:En {
//    init{
//     var firstNumber: Int = 0
//     var secondNumber: Int = 0
//     var result: Int = 0
//    }
//
//    @Before
//    fun setUp() {
//        firstNumber = 0
//        secondNumber = 0
//        result = 0
//    }
//
//    @Given("a integer {int}")
//    fun `a integer`(number: Int) {
//        firstNumber = number
//    }
//
//    @And("a second integer {int}")
//    fun `a second integer`(number: Int) {
//        secondNumber = number
//    }
//
//    @When("the numbers are added")
//    fun `the numbers are added`() {
//        result = add(firstNumber, secondNumber)
//    }
//
//    @When("the numbers are subtracted")
//    fun `the numbers are subtracted`() {
//        result = subtract(firstNumber, secondNumber)
//    }
//
//    @Then("the result is {int}")
//    fun `the result is`(result: Int) {
//        assert(result == this.result)
//    }
//
//}
