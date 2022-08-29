package backend.services

import backend.Log.log
import backend.ApplicationProperties
import backend.Constants.BASE_URL
import backend.Constants.USER
import backend.Account
import org.springframework.context.MessageSource
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine
import java.nio.charset.StandardCharsets.UTF_8
import java.util.Locale.forLanguageTag
import javax.mail.MessagingException


@Service("mailService")
class MailService(
    private val properties: ApplicationProperties,
    private val mailSender: JavaMailSender,
    private val messageSource: MessageSource,
    private val templateEngine: SpringWebFluxTemplateEngine
) {
    @Async
    fun sendEmail(
        to: String,
        subject: String,
        content: String,
        isMultipart: Boolean,
        isHtml: Boolean
    ) = mailSender
        .createMimeMessage().run {
            try {
                MimeMessageHelper(
                    this,
                    isMultipart,
                    UTF_8.name()
                ).apply {
                    setTo(to)
                    setFrom(properties.mail.from)
                    setSubject(subject)
                    setText(content, isHtml)
                }
                mailSender.send(this)
                log.debug("Sent email to User '$to'")
            } catch (e: MailException) {
                log.warn("Email could not be sent to user '$to'", e)
            } catch (e: MessagingException) {
                log.warn("Email could not be sent to user '$to'", e)
            }
        }

    @Async
    fun sendEmailFromTemplate(
        account: Account,
        templateName: String,
        titleKey: String
    ) {
        when (account.email) {
            null -> {
                log.debug("Email doesn't exist for user '${account.login}'")
                return
            }
            else -> forLanguageTag(account.langKey).apply {
                sendEmail(
                    account.email!!,
                    messageSource.getMessage(titleKey, null, this),
                    templateEngine.process(
                        templateName,
                        Context(this).apply {
                            setVariable(USER, account)
                            setVariable(BASE_URL, properties.mail.baseUrl)
                        }
                    ),
                    isMultipart = false,
                    isHtml = true
                )
            }
        }
    }

    @Async
    fun sendActivationEmail(account: Account): Unit = log
        .debug(
            "Sending activation email to '{}'",
            account.email
        ).run {
            sendEmailFromTemplate(
                account,
                "mail/activationEmail",
                "email.activation.title"
            )
        }

    @Async
    fun sendCreationEmail(account: Account): Unit = log
        .debug("Sending creation email to '${account.email}'").run {
            sendEmailFromTemplate(
                account,
                "mail/creationEmail",
                "email.activation.title"
            )
        }

    @Async
    fun sendPasswordResetMail(account: Account): Unit = log
        .debug("Sending password reset email to '${account.email}'").run {
            sendEmailFromTemplate(
                account,
                "mail/passwordResetEmail",
                "email.reset.title"
            )
        }
}