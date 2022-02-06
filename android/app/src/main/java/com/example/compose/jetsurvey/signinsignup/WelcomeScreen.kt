package com.example.compose.jetsurvey.signinsignup

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ContentAlpha.medium
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.R.drawable.ic_logo_dark
import com.example.compose.jetsurvey.R.drawable.ic_logo_light
import com.example.compose.jetsurvey.R.string.*
import com.example.compose.jetsurvey.signinsignup.WelcomeEvent.SignInSignUp
import com.example.compose.jetsurvey.theme.JetsurveyTheme
import com.example.compose.jetsurvey.util.supportWideScreen

sealed class WelcomeEvent {
    data class SignInSignUp(val email: String) : WelcomeEvent()
}

@Composable
fun WelcomeScreen(onEvent: (WelcomeEvent) -> Unit) {
    var brandingBottom by remember { mutableStateOf(0f) }
    var showBranding by remember { mutableStateOf(true) }
    var heightWithBranding by remember { mutableStateOf(0) }

    val currentOffsetHolder = remember { mutableStateOf(0f) }
    currentOffsetHolder.value = if (showBranding) 0f else -brandingBottom
    val currentOffsetHolderDp =
        with(receiver = LocalDensity.current) { currentOffsetHolder.value.toDp() }
    val heightDp = with(receiver = LocalDensity.current) { heightWithBranding.toDp() }
    Surface(modifier = Modifier.supportWideScreen()) {
        val offset by animateDpAsState(targetValue = currentOffsetHolderDp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .brandingPreferredHeight(
                    showBranding = showBranding,
                    heightDp = heightDp
                )
                .offset(y = offset)
                .onSizeChanged {
                    if (showBranding) heightWithBranding = it.height
                }
        ) {
            Branding(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f)
                    .onGloballyPositioned {
                        if (brandingBottom == 0f)
                            brandingBottom = it.boundsInParent().bottom
                    }
            )
            SignInCreateAccount(
                onEvent = onEvent,
                onFocusChange = { focused -> showBranding = !focused },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }
    }
}

private fun Modifier.brandingPreferredHeight(
    showBranding: Boolean,
    heightDp: Dp
): Modifier = if (!showBranding) this
    .wrapContentHeight(unbounded = true)
    .height(height = heightDp) else this

@Composable
private fun Branding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.wrapContentHeight(align = CenterVertically)
    ) {
        //turn to true to show logo
        val isShowLogo = true
        if (isShowLogo) Logo(
            modifier = Modifier
                .align(alignment = CenterHorizontally)
                .padding(horizontal = 76.dp)
        )
        Text(
            text = stringResource(id = app_tagline),
            style = typography.subtitle1,
            textAlign = Center,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun Logo(
    modifier: Modifier = Modifier,
    lightTheme: Boolean = colors.isLight
) = Image(
    painter = painterResource(id = if (lightTheme) ic_logo_light else ic_logo_dark),
    modifier = modifier,
    contentDescription = null
)

@Composable
private fun SignInCreateAccount(
    onEvent: (WelcomeEvent) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val emailState = remember { EmailState() }
    Column(modifier = modifier, horizontalAlignment = CenterHorizontally) {
        CompositionLocalProvider(LocalContentAlpha provides medium) {
            Text(
                text = stringResource(id = sign_in_create_account),
                style = typography.subtitle2,
                textAlign = Center,
                modifier = Modifier.padding(top = 64.dp, bottom = 12.dp)
            )
        }
        val onSubmit = {
            if (emailState.isValid) onEvent(SignInSignUp(emailState.text))
            else emailState.enableShowErrors()
        }
        onFocusChange(emailState.isFocused)
        Email(
            emailState = emailState,
            imeAction = ImeAction.Done,
            onImeAction = onSubmit
        )
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 3.dp)
        ) {
            Text(
                text = stringResource(id = user_continue),
                style = typography.subtitle2
            )
        }
    }
}

@Preview(name = "Welcome light theme")
@Composable
fun WelcomeScreenPreview() = JetsurveyTheme { WelcomeScreen {} }

@Preview(name = "Welcome dark theme")
@Composable
fun WelcomeScreenPreviewDark() = JetsurveyTheme(darkTheme = true) { WelcomeScreen {} }
