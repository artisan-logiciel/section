package reaktive.config

import org.springframework.beans.factory.getBean
import org.springframework.boot.SpringApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.MessageSource
import reaktive.ReaktiveApp
import reaktive.config.Constants.DEV_HOST
import reaktive.config.Constants.SPRING_PROFILE_CLOUD
import reaktive.config.Constants.SPRING_PROFILE_CONF_DEFAULT_KEY
import reaktive.config.Constants.SPRING_PROFILE_DEVELOPMENT
import reaktive.config.Constants.SPRING_PROFILE_PRODUCTION
import reaktive.config.Constants.STARTUP_LOG_MSG_KEY
import reaktive.config.Log.log
import java.net.InetAddress.getLocalHost
import java.net.UnknownHostException
import java.util.Locale.getDefault

object BootRunUtil {

    @JvmStatic
    fun start(args: Array<String>): Unit =
        runApplication<ReaktiveApp>(*args) {
            loader(app = this)
        }.run { startupLog(context = this) }

    @JvmStatic
    fun loader(app: SpringApplication): Unit =
        with(app) {
            setDefaultProperties(
                hashMapOf<String, Any>().apply {
                    set(
                        SPRING_PROFILE_CONF_DEFAULT_KEY,
                        SPRING_PROFILE_DEVELOPMENT
                    )
                })
            setAdditionalProfiles(SPRING_PROFILE_DEVELOPMENT)
        }

    @JvmStatic
    fun checkProfileLog(context: ApplicationContext): Array<String> =
        context.environment.activeProfiles.apply {
            if (contains(SPRING_PROFILE_DEVELOPMENT) &&
                contains(SPRING_PROFILE_PRODUCTION)
            ) log.error(
                context.getBean<MessageSource>().getMessage(
                    STARTUP_LOG_MSG_KEY,
                    arrayOf(
                        SPRING_PROFILE_DEVELOPMENT,
                        SPRING_PROFILE_PRODUCTION
                    ),
                    getDefault()
                )
            )
            if (contains(SPRING_PROFILE_DEVELOPMENT) &&
                contains(SPRING_PROFILE_CLOUD)
            ) log.error(
                context.getBean<MessageSource>().getMessage(
                    STARTUP_LOG_MSG_KEY,
                    arrayOf(
                        SPRING_PROFILE_DEVELOPMENT,
                        SPRING_PROFILE_CLOUD
                    ),
                    getDefault()
                )
            )
        }


    @JvmStatic
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


    @JvmStatic
    val evaluatedHostAddress: String
        get() {
            try {
                return getLocalHost().hostAddress
            } catch (e: UnknownHostException) {
                log.warn(
                    "The host name could not be determined, " +
                            "using `localhost` as fallback"
                )
            }
            return DEV_HOST
        }


    @JvmStatic
    private fun startupLogMessage(
        appName: String?,
        protocol: String,
        serverPort: String?,
        contextPath: String,
        hostAddress: String,
        profiles: String
    ): String = """${"\n\n\n"}
    ----------------------------------------------------------
    Application '${appName}' is running! Access URLs:
    Local:      $protocol://localhost:$serverPort$contextPath
    External:   $protocol://$hostAddress:$serverPort$contextPath
    Profile(s): $profiles
    ----------------------------------------------------------
    ${"\n\n\n"}""".trimIndent()
}


