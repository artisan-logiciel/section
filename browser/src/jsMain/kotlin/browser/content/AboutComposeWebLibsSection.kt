package browser.content

import androidx.compose.runtime.Composable
import browser.components.Card
import browser.components.ContainerInSection
import browser.components.LinkOnCard
import browser.style.WtCols.wtCol3
import browser.style.WtCols.wtCol6
import browser.style.WtCols.wtColMd6
import browser.style.WtCols.wtColSm12
import browser.style.WtOffsets.wtTopOffset12
import browser.style.WtOffsets.wtTopOffset24
import browser.style.WtOffsets.wtTopOffset48
import browser.style.WtOffsets.wtTopOffsetSm24
import browser.style.WtRows.wtRow
import browser.style.WtRows.wtRowSizeM
import browser.style.WtSections.wtSectionBgGrayLight
import browser.style.WtTexts.wtButton
import browser.style.WtTexts.wtButtonThemeLight
import browser.style.WtTexts.wtH2
import browser.style.WtTexts.wtText1
import browser.style.WtTexts.wtText1HardnessHard
import browser.style.WtTexts.wtText2
import org.jetbrains.compose.web.attributes.ATarget.Blank
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.paddingTop
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.*

data class CardWithListPresentation(
    val title: String,
    val list: List<String>,
    val links: List<LinkOnCard> = emptyList()
)

private fun createAboutComposeWebCards(): List<CardWithListPresentation> {
    return listOf(
        CardWithListPresentation(
            title = "Composable DOM API",
            list = listOf(
                "Express your design and layout in terms of DOM elements and HTML tags",
                "Use a type-safe HTML DSL to build your UI representation",
                "Get full control over the look and feel of your application by creating stylesheets with a typesafe CSS DSL",
                "Integrate with other JavaScript libraries via DOM subtrees"
            )
        ),
        CardWithListPresentation(
            title = "Multiplatform Widgets With Web Support",
            list = listOf(
                "Use and build Compose widgets that work on Android, Desktop, and Web by utilizing Kotlin's expect-actual mechanisms to provide platform-specific implementations",
                "Experiment with a set of layout primitives and APIs that mimic the features you already know from Compose for Desktop and Android"
            )
        )
    )
}

private fun createFeaturesList(): List<String> = listOf(
    "Same reactive engine that is used on Android/Desktop allows using a common codebase.",
    "Framework for rich UI creation for Kotlin/JS.",
    "Convenient Kotlin DOM DSL that covers all common frontend development scenarios.",
    "Comprehensive CSS-in-Kotlin/JS API."
)


@Composable
private fun FeatureDescriptionBlock(description: String) =
    Div(attrs = {
        classes(
            wtCol3,
            wtColMd6,
            wtColSm12,
            wtTopOffset48
        )
    }) {
        Img(src = "compose_bullet.svg")
        P(attrs = {
            classes(
                wtText1,
                wtText1HardnessHard,
                wtTopOffset12
            )
        }) { Text(description) }
    }

@Composable
fun ComposeWebLibraries() {
    ContainerInSection(wtSectionBgGrayLight) {
        H2(attrs = { classes(wtH2) }) {
            Text("Building user interfaces with Compose for Web")
        }

        Div(attrs = {
            classes(wtRow, wtRowSizeM)
        }) {
            Div(attrs = {
                classes(
                    wtCol6,
                    wtColMd6,
                    wtColSm12,
                    wtTopOffset24
                )
            }) {
                P(attrs = { classes(wtText1) }) {
                    Text("Compose for Web allows you to build reactive user interfaces for the web in Kotlin, using the concepts and APIs of Jetpack Compose to express the state, behavior, and logic of your application.")
                }
            }
        }

        Div(attrs = {
            classes(
                wtRow,
                wtRowSizeM,
                wtTopOffset24
            )
        }) { createFeaturesList().forEach { FeatureDescriptionBlock(it) } }

        A(
            attrs = {
                classes(
                    wtButton,
                    wtButtonThemeLight,
                    wtTopOffset48,
                    wtTopOffsetSm24
                )
                target(Blank)
            },
            href = "https://github.com/JetBrains/compose-jb/blob/master/FEATURES.md#features-currently-available-in-compose-for-web"
        ) {
            Text("See all features")
        }
    }
}

@Composable
private fun CardWithList(card: CardWithListPresentation) {
    Card(
        title = card.title,
        links = card.links
    ) {
        Ul(attrs = { classes(wtText2) }) {
            card.list.forEachIndexed { _, it ->
                Li({ style { paddingTop(24.px) } }) {
                    Text(it)
                }
            }
        }
    }
}