package browser.content

import androidx.compose.runtime.Composable
import browser.components.ContainerInSection
import browser.style.WtCols.wtCol9
import browser.style.WtCols.wtColMd11
import browser.style.WtCols.wtColSm12
import browser.style.WtOffsets.wtTopOffset24
import browser.style.WtRows.wtRow
import browser.style.WtRows.wtRowSizeM
import browser.style.WtSections.wtSectionBgGrayLight
import browser.style.WtTexts.wtButton
import browser.style.WtTexts.wtButtonContrast
import browser.style.WtTexts.wtLink
import browser.style.WtTexts.wtSubtitle2
import org.jetbrains.compose.web.attributes.ATarget.Blank
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
fun JoinUs() {
    ContainerInSection(wtSectionBgGrayLight) {
        Div(attrs = {
            classes(wtRow, wtRowSizeM)
        }) {
            Div(attrs = {
                classes(wtCol9, wtColMd11, wtColSm12)
            }) {

                P(attrs = {
                    classes(wtSubtitle2)
                }) {
                    Text("Interested in Compose for other platforms?")

                    P {
                        Text("Have a look at ")
                        A(href = "https://www.jetbrains.com/lp/compose/", attrs = {
                            classes(wtLink)
                            target(Blank)
                        }) {
                            Text("Compose Multiplatform")
                        }
                    }
                }

                P(attrs = {
                    classes(wtSubtitle2, wtTopOffset24)
                }) {
                    Text("Feel free to join the ")
                    LinkToSlack(
                        url = "https://kotlinlang.slack.com/archives/C01F2HV7868",
                        text = "#compose-web"
                    )
                    Text(" channel on Kotlin Slack to discuss Compose for Web, or ")
                    LinkToSlack(
                        url = "https://kotlinlang.slack.com/archives/CJLTWPH7S",
                        text = "#compose"
                    )
                    Text(" for general Compose discussions")
                }
            }
        }

        A(attrs = {
            classes(wtButton, wtButtonContrast, wtTopOffset24)
            target(Blank)
        }, href = "https://surveys.jetbrains.com/s3/kotlin-slack-sign-up") {
            Text("Join Kotlin Slack")
        }
    }
}

@Composable
private fun LinkToSlack(url: String, text: String) {
    A(href = url, attrs = {
        target(Blank)
        classes(wtLink)
    }) {
        Text(text)
    }
}
