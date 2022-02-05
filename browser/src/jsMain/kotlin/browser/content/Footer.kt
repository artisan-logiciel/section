package browser.content

import androidx.compose.runtime.Composable
import browser.style.WtCols.wtColInline
import browser.style.WtContainer.wtContainer
import browser.style.WtOffsets.wtTopOffset48
import browser.style.WtRows.wtRow
import browser.style.WtRows.wtRowSizeM
import browser.style.WtRows.wtRowSmAlignItemsCenter
import browser.style.WtSections.wtSectionBgGrayDark
import browser.style.WtTexts.wtSocialButtonItem
import browser.style.WtTexts.wtText1
import browser.style.WtTexts.wtText1ThemeDark
import browser.style.WtTexts.wtText3
import browser.style.WtTexts.wtTextPale
import org.jetbrains.compose.web.attributes.ATarget.Blank
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.FlexWrap.Companion.Wrap
import org.jetbrains.compose.web.css.JustifyContent.Companion.Center
import org.jetbrains.compose.web.css.JustifyContent.Companion.SpaceEvenly
import org.jetbrains.compose.web.dom.*


@Composable
fun PageFooter() {
    Footer({
        style {
            flexShrink(0)
            boxSizing("border-box")
        }
    }) {
        Section({
            classes(wtSectionBgGrayDark)
            style {
                padding(24.px, 0.px)
            }
        }) {
            Div({ classes(wtContainer) }) {
                Div({
                    classes(wtRow, wtRowSizeM, wtRowSmAlignItemsCenter)
                    style {
                        justifyContent(Center)
                        flexWrap(Wrap)
                    }
                }) {

                    Div({
                        classes(wtColInline)
                    }) {
                        P({
                            classes(wtText1, wtText1ThemeDark)
                        }) {
                            Text("Follow us")
                        }
                    }

                    Div({
                        classes(wtColInline)
                    }) {
                        getSocialLinks().forEach { SocialIconLink(it) }
                    }
                }

                CopyrightInFooter()
            }
        }
    }
}

@Composable
private fun CopyrightInFooter() {
    Div({
        classes(
            wtRow,
            wtRowSizeM,
            wtRowSmAlignItemsCenter,
            wtTopOffset48
        )
        style {
            justifyContent(SpaceEvenly)
            flexWrap(Wrap)
            padding(0.px, 12.px)
        }
    }) {
        Span({
            classes(wtText3, wtTextPale)
        }) {
            Text("Copyright © 2000-2021  JetBrains s.r.o.")
        }

        Span({
            classes(wtText3, wtTextPale)
        }) {
            Text("Developed with drive and IntelliJ IDEA")
        }
    }
}

@Composable
private fun SocialIconLink(link: SocialLink) {
    A(attrs = {
        classes(wtSocialButtonItem)
        target(Blank)
    }, href = link.url) {
        Img(src = link.iconSvg) {}
    }
}

private data class SocialLink(
    val id: String,
    val url: String,
    val title: String,
    val iconSvg: String
)

private fun getSocialLinks(): List<SocialLink> = listOf(
    SocialLink(
        "facebook",
        "https://www.facebook.com/JetBrains",
        "JetBrains on Facebook",
        "ic_fb.svg"
    ),
    SocialLink(
        "twitter",
        "https://twitter.com/jetbrains",
        "JetBrains on Twitter",
        "ic_twitter.svg"
    ),
    SocialLink(
        "linkedin",
        "https://www.linkedin.com/company/jetbrains",
        "JetBrains on Linkedin",
        "ic_linkedin.svg"
    ),
    SocialLink(
        "youtube",
        "https://www.youtube.com/user/JetBrainsTV",
        "JetBrains on YouTube",
        "ic_youtube.svg"
    ),
    SocialLink(
        "instagram",
        "https://www.instagram.com/jetbrains/",
        "JetBrains on Instagram",
        "ic_insta.svg"
    ),
    SocialLink(
        "blog",
        "https://blog.jetbrains.com/",
        "JetBrains blog",
        "ic_jb_blog.svg"
    ),
    SocialLink(
        "rss",
        "https://blog.jetbrains.com/feed/",
        "JetBrains RSS Feed",
        "ic_feed.svg"
    ),
)