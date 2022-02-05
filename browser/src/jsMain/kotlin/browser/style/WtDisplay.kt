package browser.style

import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.DisplayStyle.Companion.Block
import org.jetbrains.compose.web.css.DisplayStyle.Companion.None

object WtDisplay : StyleSheet(AppStylesheet) {
    val wtDisplayNone by style { display(None) }

    val wtDisplayMdBlock by style {
        media(mediaMaxWidth(1000.px)) {
            self style { display(Block) }
        }
    }

    val wtDisplayMdNone by style {
        media(mediaMaxWidth(1000.px)) {
            self style { display(None) }
        }
    }
}
