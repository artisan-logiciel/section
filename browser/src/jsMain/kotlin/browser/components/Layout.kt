package browser.components

import androidx.compose.runtime.Composable
import browser.style.WtContainer.wtContainer
import browser.style.WtOffsets.wtTopOffset96
import browser.style.WtSections.wtSection
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.DisplayStyle.Companion.Flex
import org.jetbrains.compose.web.css.FlexDirection.Companion.Column
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Main
import org.jetbrains.compose.web.dom.Section

@Composable
fun Layout(content: @Composable () -> Unit) {
    Div({
        style {
            display(displayStyle = Flex)
            flexDirection(Column)
            height(100.percent)
            margin(0.px)
            boxSizing("border-box")
        }
    }) {
        content()
    }
}

@Composable
fun MainContentLayout(content: @Composable () -> Unit) {
    Main({
        style {
            flex("1 0 auto")
            boxSizing("border-box")
        }
    }) {
        content()
    }
}

@Composable
fun ContainerInSection(
    sectionThemeStyleClass: String? = null,
    content: @Composable () -> Unit
) {
    Section({
        if (sectionThemeStyleClass != null)
            classes(wtSection, sectionThemeStyleClass)
        else classes(wtSection)
    }) { Div({ classes(wtContainer, wtTopOffset96) }) { content() } }
}