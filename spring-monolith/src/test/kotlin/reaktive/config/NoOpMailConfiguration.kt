package reaktive.config

import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reaktive.repositories.entities.User
import reaktive.services.MailService

@Configuration
class NoOpMailConfiguration {
    private val mockMailService = mock(MailService::class.java)

    @Bean
    fun mailService(): MailService = mockMailService

    init {
        doNothing()
            .`when`(mockMailService)
            .sendActivationEmail(User())
    }
}