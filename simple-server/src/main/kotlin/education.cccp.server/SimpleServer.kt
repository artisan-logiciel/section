package education.cccp.server

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.html.*

fun HTML.index() {
    head {
        title("Hello from Ktor!")
    }
    body {
        div {
            +"Hello from Ktor"
        }
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        routing {
            get("/") {
                call.respondHtml(OK, block = HTML::index)
            }
        }
    }.start(wait = true)
}