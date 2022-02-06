package com.example.compose.jetsurvey.signinsignup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import education.cccp.compose.popular.Screen.Welcome
import education.cccp.compose.popular.navigate
import com.example.compose.jetsurvey.signinsignup.WelcomeEvent.SignInSignUp
import com.example.compose.jetsurvey.theme.JetsurveyTheme

/**
 * Fragment containing the welcome UI.
 */
class WelcomeFragment : Fragment() {

    private val viewModel: WelcomeViewModel by viewModels { WelcomeViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Welcome)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                JetsurveyTheme {
                    WelcomeScreen(onEvent = { event ->
                        when (event) {
                            is SignInSignUp -> viewModel.handleContinue(event.email)
                        }
                    })
                }
            }
        }
    }
}
