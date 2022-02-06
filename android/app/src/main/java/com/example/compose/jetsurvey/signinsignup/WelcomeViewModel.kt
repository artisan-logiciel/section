package com.example.compose.jetsurvey.signinsignup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import education.cccp.compose.popular.Screen
import education.cccp.compose.popular.Screen.SignIn
import education.cccp.compose.popular.Screen.SignUp
import com.example.compose.jetsurvey.util.Event

class WelcomeViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo

    fun handleContinue(email: String): Unit =
        if (userRepository.isKnownUserEmail(email))
            _navigateTo.value = Event(SignIn)
        else _navigateTo.value = Event(SignUp)
}

class WelcomeViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WelcomeViewModel::class.java))
            return WelcomeViewModel(UserRepository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
