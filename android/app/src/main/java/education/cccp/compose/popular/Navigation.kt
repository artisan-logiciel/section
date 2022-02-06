package education.cccp.compose.popular

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.compose.jetsurvey.R.id.*
import education.cccp.compose.popular.Screen.*
import java.security.InvalidParameterException

enum class Screen { Welcome, SignUp, SignIn, Survey }

fun Fragment.navigate(to: Screen, from: Screen) {
    if (to == from) throw InvalidParameterException("Can't navigate to $to")
    when (to) {
        Welcome -> findNavController().navigate(welcome_fragment)
        SignUp -> findNavController().navigate(sign_up_fragment)
        SignIn -> findNavController().navigate(sign_in_fragment)
        Survey -> findNavController().navigate(survey_fragment)
    }
}
