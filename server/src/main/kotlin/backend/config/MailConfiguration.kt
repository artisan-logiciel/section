package backend.config

import org.apache.commons.mail.EmailConstants.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import backend.properties.ApplicationProperties

@Suppress("unused")
@Configuration
class MailConfiguration(private val properties: ApplicationProperties) {

    @Bean
    fun javaMailSender(): JavaMailSender = JavaMailSenderImpl()
        .apply {
            host = properties.mail.host
            port = properties.mail.port
            username = properties.mail.from
            password = properties.mail.password
            javaMailProperties.apply {
                this[MAIL_TRANSPORT_PROTOCOL] = properties.mail.property.transport.protocol
                this[MAIL_SMTP_AUTH] = properties.mail.property.smtp.auth
                this[MAIL_TRANSPORT_STARTTLS_ENABLE] = properties.mail.property.smtp.starttls.enable
                this[MAIL_DEBUG] = properties.mail.property.debug
            }
        }
}