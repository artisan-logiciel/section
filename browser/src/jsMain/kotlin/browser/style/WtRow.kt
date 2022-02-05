package browser.style

import browser.style.AppCSSVariables.wtHorizontalLayoutGutter
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems.Companion.Center
import org.jetbrains.compose.web.css.DisplayStyle.Companion.Flex
import org.jetbrains.compose.web.css.FlexWrap.Companion.Wrap

object WtRows : StyleSheet(AppStylesheet) {

    val wtRow by style {
        AppCSSVariables.wtHorizontalLayoutGutter(0.px)
        display(Flex)
        flexWrap(Wrap)

        property(
            "margin-right",
            "calc(-1*${wtHorizontalLayoutGutter.value()})"
        )
        property(
            "margin-left",
            "calc(-1*${wtHorizontalLayoutGutter.value()})"
        )
        boxSizing("border-box")
    }

    val wtRowSizeM by style {
        AppCSSVariables.wtHorizontalLayoutGutter(16.px)

        media(mediaMaxWidth(640.px)) {
            self style {
                AppCSSVariables.wtHorizontalLayoutGutter(8.px)
            }
        }
    }

    val wtRowSizeXs by style {
        AppCSSVariables.wtHorizontalLayoutGutter(6.px)
    }

    val wtRowSmAlignItemsCenter by style {
        self style {
            alignItems(Center)
        }
    }
}
