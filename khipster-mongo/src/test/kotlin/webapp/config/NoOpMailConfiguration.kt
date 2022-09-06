package webapp.config

import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import webapp.domain.User
import webapp.service.MailService

@Configuration
class NoOpMailConfiguration {
    private val mockMailService = Mockito.mock(MailService::class.java)

    @Bean
    fun mailService(): MailService {
        return mockMailService
    }

    init {
        Mockito.doNothing().`when`(mockMailService).sendActivationEmail(User())
    }
}
