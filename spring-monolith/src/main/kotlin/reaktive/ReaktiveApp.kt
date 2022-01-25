package reaktive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import reaktive.config.BootRunUtil.checkProfileLog
import reaktive.config.BootRunUtil.start
import reaktive.properties.ApplicationProperties
import javax.annotation.PostConstruct

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties::class)
class ReaktiveApp(
    private val context: ApplicationContext
) {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = start(args)
    }

    @PostConstruct
    private fun init(): Array<String> = checkProfileLog(context = context)
}