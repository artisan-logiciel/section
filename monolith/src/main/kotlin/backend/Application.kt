package backend

import backend.Log.log
import backend.config.Constants.DEV_HOST
import backend.config.Constants.SPRING_PROFILE_CLOUD
import backend.config.Constants.SPRING_PROFILE_CONF_DEFAULT_KEY
import backend.config.Constants.SPRING_PROFILE_DEVELOPMENT
import backend.config.Constants.SPRING_PROFILE_PRODUCTION
import backend.config.Constants.STARTUP_LOG_MSG_KEY
import backend.config.ApplicationProperties
import org.apache.logging.log4j.LogManager.getLogger
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.getBean
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.MessageSource
import java.net.InetAddress.getLocalHost
import java.net.UnknownHostException
import java.util.Locale.getDefault
import javax.annotation.PostConstruct

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties::class)
class Server(private val context: ApplicationContext) {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = start(args)

        @JvmStatic
        fun start(args: Array<String>): Unit =
            runApplication<Server>(*args) {
                loader(app = this)
            }.run { startupLog(context = this) }

        @JvmStatic
        fun loader(app: SpringApplication): Unit =
            with(app) {
                setDefaultProperties(
                    hashMapOf<String, Any>().apply {
                        set(
                            key = SPRING_PROFILE_CONF_DEFAULT_KEY,
                            value = SPRING_PROFILE_DEVELOPMENT
                        )
                    }
                )
                setAdditionalProfiles(SPRING_PROFILE_DEVELOPMENT)
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
        fun checkProfileLog(context: ApplicationContext): Array<String> =
            context.environment.activeProfiles.apply {
                if (contains(element = SPRING_PROFILE_DEVELOPMENT) &&
                    contains(element = SPRING_PROFILE_PRODUCTION)
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

    }

    @PostConstruct
    fun init(): Array<String> = checkProfileLog(context = context)
}