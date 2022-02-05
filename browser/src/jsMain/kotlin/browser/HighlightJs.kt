package browser

import org.w3c.dom.HTMLElement

@JsNonModule
@JsName(name = "hljs")
@JsModule(import = "highlight.js")
external class HighlightJs {
    companion object {
        fun highlightElement(block: HTMLElement)
    }
}