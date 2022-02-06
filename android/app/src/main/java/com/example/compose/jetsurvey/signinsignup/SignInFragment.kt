package com.example.compose.jetsurvey.signinsignup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.compose.jetsurvey.R.id.sign_in_fragment

import com.example.compose.jetsurvey.signinsignup.SignInEvent.NavigateBack
import com.example.compose.jetsurvey.signinsignup.SignInEvent.SignUp
import com.example.compose.jetsurvey.theme.JetsurveyTheme
import education.cccp.compose.popular.navigate
import education.cccp.compose.popular.Screen.SignIn

/**
 * Fragment containing the sign in UI.
 */
class SignInFragment : Fragment() {

    private val viewModel: SignInViewModel by viewModels { SignInViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, SignIn)
            }
        }

        return ComposeView(requireContext()).apply {
            // In order for savedState to work, the same ID needs to be used for all instances.
            id = sign_in_fragment

            layoutParams = LayoutParams(
                MATCH_PARENT,
                MATCH_PARENT
            )
            setContent {
                JetsurveyTheme {
                    SignIn(
                        onNavigationEvent = { event ->
                            when (event) {
                                is SignInEvent.SignIn -> viewModel.signIn(
                                    event.email,
                                    event.password
                                )
                                is SignUp -> viewModel.signUp()
                                is NavigateBack -> activity?.onBackPressedDispatcher?.onBackPressed()
                            }
                        }
                    )
                }
            }
        }
    }
}
