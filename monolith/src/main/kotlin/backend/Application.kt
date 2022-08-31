package backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import javax.annotation.PostConstruct

fun main(args: Array<String>) = start(args)


@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties::class)
class Server(private val context: ApplicationContext) {
    @PostConstruct
    fun init(): Array<String> = checkProfileLog(context = context)
}



