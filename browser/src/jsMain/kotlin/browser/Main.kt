package browser

import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable
import browser.components.*
import browser.content.*
import browser.style.AppStylesheet

fun main() {
    renderComposable(rootElementId = "root") {
        Style(AppStylesheet)

        Layout {
            Header()
            MainContentLayout {
                Intro()
                ComposeWebLibraries()
                GetStarted()
                CodeSamples()
                JoinUs()
            }
            PageFooter()
        }
    }
}