package com.example.compose.jetsurvey.signinsignup

import androidx.compose.runtime.Immutable
import com.example.compose.jetsurvey.signinsignup.User.LoggedInUser
import com.example.compose.jetsurvey.signinsignup.User.NoUserLoggedIn

sealed class User {
    @Immutable
    data class LoggedInUser(val email: String) : User()
    object NoUserLoggedIn : User()
}

/**
 * Repository that holds the logged in user.
 *
 * In a production app, this class would also handle the communication with the backend for
 * sign in and sign up.
 */
object UserRepository {

    private var _user: User = NoUserLoggedIn
    val user: User
        get() = _user

    @Suppress("UNUSED_PARAMETER")
    fun signIn(email: String, password: String) {
        _user = LoggedInUser(email)
    }

    @Suppress("UNUSED_PARAMETER")
    fun signUp(email: String, password: String) {
        _user = LoggedInUser(email)
    }

    fun isKnownUserEmail(email: String): Boolean {
        // if the email contains "sign up" we consider it unknown
        return !email.contains("signup")
    }
}
