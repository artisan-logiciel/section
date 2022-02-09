package backend.services

import org.springframework.context.MessageSource
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine
import common.config.Constants.BASE_URL
import common.config.Constants.USER
import backend.Server.Log.log
import backend.config.ApplicationProperties
//import backend.repositories.entities.User
import java.nio.charset.StandardCharsets.UTF_8
import java.util.Locale.forLanguageTag
import javax.mail.MessagingException


//@Service("mailService")
//class MailService(
//    private val properties: ApplicationProperties,
//    private val mailSender: JavaMailSender,
//    private val messageSource: MessageSource,
//    private val templateEngine: SpringWebFluxTemplateEngine
//) {
//    @Async
//    fun sendEmail(
//        to: String,
//        subject: String,
//        content: String,
//        isMultipart: Boolean,
//        isHtml: Boolean
//    ): Unit = mailSender
//        .createMimeMessage().run {
//            try {
//                MimeMessageHelper(
//                    this,
//                    isMultipart,
//                    UTF_8.name()
//                ).apply {
//                    setTo(to)
//                    setFrom(properties.mail.from)
//                    setSubject(subject)
//                    setText(content, isHtml)
//                }
//                mailSender.send(this)
//                log.debug("Sent email to User '$to'")
//            } catch (e: MailException) {
//                log.warn("Email could not be sent to user '$to'", e)
//            } catch (e: MessagingException) {
//                log.warn("Email could not be sent to user '$to'", e)
//            }
//        }
//
//    @Async
//    fun sendEmailFromTemplate(
//        user: User,
//        templateName: String,
//        titleKey: String
//    ) {
//        if (user.email == null) {
//            log.debug("Email doesn't exist for user '${user.login}'")
//            return
//        } else
//            forLanguageTag(user.langKey).apply {
//                sendEmail(
//                    user.email!!,
//                    messageSource.getMessage(titleKey, null, this),
//                    templateEngine.process(
//                        templateName,
//                        Context(this).apply {
//                            setVariable(USER, user)
//                            setVariable(BASE_URL, properties.mail.baseUrl)
//                        }
//                    ),
//                    isMultipart = false,
//                    isHtml = true
//                )
//            }
//    }
//
//    @Async
//    fun sendActivationEmail(user: User): Unit = log
//        .debug(
//            "Sending activation email to '{}'",
//            user.email
//        ).run {
//            sendEmailFromTemplate(
//                user,
//                "mail/activationEmail",
//                "email.activation.title"
//            )
//        }
//
//    @Async
//    fun sendCreationEmail(user: User): Unit = log
//        .debug("Sending creation email to '${user.email}'").run {
//            sendEmailFromTemplate(
//                user,
//                "mail/creationEmail",
//                "email.activation.title"
//            )
//        }
//
//    @Async
//    fun sendPasswordResetMail(user: User): Unit = log
//        .debug("Sending password reset email to '${user.email}'").run {
//            sendEmailFromTemplate(
//                user,
//                "mail/passwordResetEmail",
//                "email.reset.title"
//            )
//        }
//}