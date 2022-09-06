package backend

import backend.Log.log
import org.springframework.beans.factory.getBean
import org.springframework.boot.SpringApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.MessageSource
import java.net.InetAddress.*
import java.net.UnknownHostException
import java.util.*


fun main(args: Array<String>) = start(args)

fun start(args: Array<String>): Unit =
    runApplication<Server>(*args) {
        loader(app = this)
    }.run { startupLog(context = this) }

fun checkProfileLog(context: ApplicationContext): Array<String> =
    context.environment.activeProfiles.apply {
        if (contains(element = Constants.SPRING_PROFILE_DEVELOPMENT) &&
            contains(element = Constants.SPRING_PROFILE_PRODUCTION)
        ) log.error(
            context.getBean<MessageSource>().getMessage(
                Constants.STARTUP_LOG_MSG_KEY,
                arrayOf(
                    Constants.SPRING_PROFILE_DEVELOPMENT,
                    Constants.SPRING_PROFILE_PRODUCTION
                ),
                Locale.getDefault()
            )
        )
        if (contains(Constants.SPRING_PROFILE_DEVELOPMENT) &&
            contains(Constants.SPRING_PROFILE_CLOUD)
        ) log.error(
            context.getBean<MessageSource>().getMessage(
                Constants.STARTUP_LOG_MSG_KEY,
                arrayOf(
                    Constants.SPRING_PROFILE_DEVELOPMENT,
                    Constants.SPRING_PROFILE_CLOUD
                ),
                Locale.getDefault()
            )
        )
    }

fun loader(app: SpringApplication): Unit = with(app) {
    setDefaultProperties(hashMapOf<String, Any>(Constants.SPRING_PROFILE_CONF_DEFAULT_KEY to Constants.SPRING_PROFILE_DEVELOPMENT))
    setAdditionalProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)
}

fun startupLog(context: ApplicationContext): Unit =
    log.info(
        startupLogMessage(
            appName = context.environment.getProperty("spring.application.name"),
            protocol = if (context.environment.getProperty("server.ssl.key-store") != null) "https"
            else "http",
            serverPort = context.environment.getProperty("server.port"),
            contextPath = context.environment.getProperty("server.servlet.context-path") ?: "/",
            hostAddress = evaluatedHostAddress,
            profiles = context.environment.activeProfiles.joinToString(separator = ",")
        )
    )

private val evaluatedHostAddress: String
    get() {
        try {
            return getLocalHost().hostAddress
        } catch (e: UnknownHostException) {
            log.warn(
                "The host name could not be determined, " +
                        "using `localhost` as fallback"
            )
        }
        return Constants.DEV_HOST
    }

private fun startupLogMessage(
    appName: String?,
    protocol: String,
    serverPort: String?,
    contextPath: String,
    hostAddress: String,
    profiles: String
): String = """${"\n\n\n"}
----------------------------------------------------------
go visit https://www.cheroliv.com    
----------------------------------------------------------
Application '${appName}' is running! Access URLs:
Local:      $protocol://localhost:$serverPort$contextPath
External:   $protocol://$hostAddress:$serverPort$contextPath
Profile(s): $profiles
----------------------------------------------------------
${"\n\n\n"}""".trimIndent()