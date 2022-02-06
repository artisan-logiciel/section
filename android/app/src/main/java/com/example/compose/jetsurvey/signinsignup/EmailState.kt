package com.example.compose.jetsurvey.signinsignup

import java.util.regex.Pattern.matches

// Consider an email valid if there's some text before and after a "@"
private const val EMAIL_VALIDATION_REGEX = "^(.+)@(.+)\$"

class EmailState :
    TextFieldState(validator = ::isEmailValid, errorFor = ::emailValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */
private fun emailValidationError(email: String): String = "Invalid email: $email"

private fun isEmailValid(email: String): Boolean = matches(EMAIL_VALIDATION_REGEX, email)
