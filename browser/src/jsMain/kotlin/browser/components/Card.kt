package browser.components

import androidx.compose.runtime.Composable
import browser.style.WtCards.wtCard
import browser.style.WtCards.wtCardSection
import browser.style.WtCards.wtCardThemeDark
import browser.style.WtCards.wtCardThemeLight
import browser.style.WtCards.wtVerticalFlex
import browser.style.WtCards.wtVerticalFlexGrow
import browser.style.WtCols.wtCol6
import browser.style.WtCols.wtColMd6
import browser.style.WtCols.wtColSm12
import browser.style.WtOffsets.wtTopOffset24
import browser.style.WtTexts.wtH3
import browser.style.WtTexts.wtH3ThemeDark
import browser.style.WtTexts.wtLink
import org.jetbrains.compose.web.attributes.ATarget.Blank
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Text


data class LinkOnCard(
    val linkText: String,
    val linkUrl: String
)

@Composable
private fun CardTitle(
    title: String,
    darkTheme: Boolean = false
) {
    H3({
        classes(wtH3)
        if (darkTheme) {
            classes(wtH3ThemeDark)
        }
    }) {
        Text(value = title)
    }
}

@Composable
private fun CardLink(link: LinkOnCard) {
    A(
        attrs = {
            classes(wtLink, wtTopOffset24)
            target(Blank)
        },
        href = link.linkUrl
    ) {
        Text(value = link.linkText)
    }
}

@Composable
fun Card(
    title: String,
    links: List<LinkOnCard>,
    darkTheme: Boolean = false,
    wtExtraStyleClasses: List<String> = listOf(wtCol6, wtColMd6, wtColSm12),
    content: @Composable () -> Unit
) {
    Div({
        classes(
            wtCard,
            wtTopOffset24,
            *wtExtraStyleClasses.toTypedArray()
        )
        classes(if (darkTheme) wtCardThemeDark else wtCardThemeLight)
    }) {
        Div({
            classes(wtCardSection, wtVerticalFlex)
        }) {
            Div({ classes(wtVerticalFlexGrow) }) {
                CardTitle(
                    title = title,
                    darkTheme = darkTheme
                )
                content()
            }
            links.forEach { CardLink(it) }
        }
    }
}

@Composable
fun CardDark(
    title: String,
    links: List<LinkOnCard>,
    wtExtraStyleClasses: List<String> = listOf(
        wtCol6,
        wtColMd6,
        wtColSm12
    ),
    content: @Composable () -> Unit
) {
    Card(
        title = title,
        links = links,
        darkTheme = true,
        wtExtraStyleClasses = wtExtraStyleClasses,
        content = content
    )
}