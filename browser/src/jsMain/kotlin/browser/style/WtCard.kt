package browser.style

import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems.Companion.FlexStart
import org.jetbrains.compose.web.css.DisplayStyle.Companion.Flex
import org.jetbrains.compose.web.css.FlexDirection.Companion.Column
import org.jetbrains.compose.web.css.LineStyle.Companion.Solid
import org.jetbrains.compose.web.css.Position.Companion.Relative

object WtCards : StyleSheet(AppStylesheet) {
    val wtCard by style {
        display(Flex)
        flexDirection(Column)
        border(1.px, Solid)
        minHeight(0.px)
        boxSizing("border-box")
    }

    val wtCardThemeLight by style {
        border(color = rgba(39,40,44,.2))
        color(Color("#27282c"))
        backgroundColor(Color("white"))
    }

    val wtCardThemeDark by style {
        backgroundColor(rgba(255, 255, 255, 0.05))
        color(rgba(255, 255, 255, 0.6))
        border(0.px)
    }

    val wtCardSection by style {
        position(Relative)
        overflow("auto")
        flex( "1 1 auto")
        minHeight( 0.px)
        boxSizing("border-box")
        padding(24.px, 32.px)

        media(mediaMaxWidth(640.px)) {
            self style { padding(16.px) }
        }
    }

    val wtVerticalFlex by style {
        display(Flex)
        flexDirection(Column)
        alignItems(FlexStart)
    }

    val wtVerticalFlexGrow by style {
        flexGrow(1)
        maxWidth(100.percent)
    }
}
