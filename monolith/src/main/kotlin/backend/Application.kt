@file:Suppress("unused")

package backend

import backend.Constants.NORMAL_TERMINATION
import backend.Constants.PROFILE_CLI
import backend.Constants.PROFILE_CLI_PROPS
import backend.Constants.SPRING_PROFILE_CONF_DEFAULT_KEY
import backend.Constants.SPRING_PROFILE_DEVELOPMENT
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import kotlin.system.exitProcess

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties::class)
class BackendApplication

object BackendBootstrap {
    @JvmStatic
    fun main(args: Array<String>) = runApplication<BackendApplication>(*args) {
        setDefaultProperties(hashMapOf<String, Any>(SPRING_PROFILE_CONF_DEFAULT_KEY to SPRING_PROFILE_DEVELOPMENT))
        setAdditionalProfiles(SPRING_PROFILE_DEVELOPMENT)
    }.run { bootstrapLog(context = this) }
}

object CliBootstrap {
    @JvmStatic
    fun main(args: Array<String>) {
        runApplication<BackendApplication>(*args) {
            setAdditionalProfiles(PROFILE_CLI)
            setDefaultProperties(PROFILE_CLI_PROPS)
        }
        exitProcess(NORMAL_TERMINATION)
    }
}