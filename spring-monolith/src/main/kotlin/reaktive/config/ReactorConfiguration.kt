package reaktive.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import reactor.core.publisher.Hooks.onOperatorDebug
import reaktive.config.Constants.SPRING_PROFILE_PRODUCTION

@Suppress("unused")
@Configuration
@Profile("!$SPRING_PROFILE_PRODUCTION")
class ReactorConfiguration {
    fun reactorConfiguration() = onOperatorDebug()
}