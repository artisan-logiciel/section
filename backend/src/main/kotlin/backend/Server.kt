package backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import backend.config.BootRunUtil.checkProfileLog
import backend.config.BootRunUtil.start
import backend.properties.ApplicationProperties
import javax.annotation.PostConstruct

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties::class)
class Server(
    private val context: ApplicationContext
) {

    @PostConstruct @Suppress("unused")
    private fun init(): Array<String> = checkProfileLog(context = context)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = start(args)
    }
}