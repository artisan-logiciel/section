package com.example.compose.jetsurvey.signinsignup

class PasswordState : TextFieldState(
    validator = ::isPasswordValid,
    errorFor = ::passwordValidationError
)

class ConfirmPasswordState(private val passwordState: PasswordState) : TextFieldState() {
    override val isValid
        get() = passwordAndConfirmationValid(passwordState.text, text)

    override fun getError(): String? =
        if (showErrors()) passwordConfirmationError()
        else null
}

private fun passwordAndConfirmationValid(password: String, confirmedPassword: String)
        : Boolean = isPasswordValid(password) && password == confirmedPassword

private fun isPasswordValid(password: String)
        : Boolean = password.length > 3

@Suppress("UNUSED_PARAMETER")
private fun passwordValidationError(password: String)
        : String = "Invalid password"

private fun passwordConfirmationError()
        : String = "Passwords don't match"