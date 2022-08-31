@file:Suppress("unused")

package backend

import backend.Constants.BASE_URL
import backend.Constants.USER
import backend.Log.log
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.context.MessageSource
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine
import java.nio.charset.StandardCharsets.UTF_8
import java.security.SecureRandom
import java.util.Locale.forLanguageTag
import javax.mail.MessagingException

@Service
class SignUpService(
    private val accountRepository: IAccountModelRepository,
    private val mailService: MailService
) {

    @Throws(
        InvalidPasswordException::class,
        UsernameAlreadyUsedException::class,
        UsernameAlreadyUsedException::class
    )
    suspend fun signup(model: AccountCredentialsModel) {
        InvalidPasswordException().run {
            if (isPasswordLengthInvalid(model.password)) throw this
        }
        loginValidation(model)
        emailValidation(model)
        model.copy(
            //TODO:hash password
            activationKey = RandomUtils.generateActivationKey
        ).run {
            accountRepository.signup(this)
            mailService.sendActivationEmail(this)
        }
    }

    @Throws(UsernameAlreadyUsedException::class)
    private suspend fun loginValidation(model: AccountCredentialsModel) {
        accountRepository.findOneByLogin(model.login!!).run {
            if (this != null) when {
                !activated -> accountRepository.suppress(this)
                else -> throw UsernameAlreadyUsedException()
            }
        }
    }

    @Throws(UsernameAlreadyUsedException::class)
    private suspend fun emailValidation(model: AccountCredentialsModel) {
        accountRepository.findOneByEmail(model.email!!).run {
            if (this != null) {
                when {
                    !activated -> accountRepository.suppress(this)
                    else -> throw EmailAlreadyUsedException()
                }
            }
        }
    }

    private suspend fun suppress(model: AccountCredentialsModel) {
        accountRepository.suppress(model.toAccount())
    }

    suspend fun activateRegistration(key: String): Boolean {
        accountRepository.run {
            with(findOneActivationKey(key)) {
                when {
                    this == null -> return false
                    else -> {
                        save(copy(activated = true, activationKey = null))
                        return true
                    }
                }
            }
        }
    }
}

object RandomUtils {
    private const val DEF_COUNT = 20
    private val SECURE_RANDOM: SecureRandom by lazy {
        SecureRandom().apply { nextBytes(ByteArray(size = 64)) }
    }

    private val generateRandomAlphanumericString: String
        get() = RandomStringUtils.random(
            DEF_COUNT, 0, 0, true, true, null, SECURE_RANDOM
        )

    val generatePassword: String
        get() = generateRandomAlphanumericString

    val generateActivationKey: String
        get() = generateRandomAlphanumericString

    val generateResetKey: String
        get() = generateRandomAlphanumericString
}

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
        account: AccountCredentialsModel,
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
                    account.email,
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
    fun sendActivationEmail(account: AccountCredentialsModel): Unit = log
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
    fun sendCreationEmail(account: AccountCredentialsModel): Unit = log
        .debug("Sending creation email to '${account.email}'").run {
            sendEmailFromTemplate(
                account,
                "mail/creationEmail",
                "email.activation.title"
            )
        }

    @Async
    fun sendPasswordResetMail(account: AccountCredentialsModel): Unit = log
        .debug("Sending password reset email to '${account.email}'").run {
            sendEmailFromTemplate(
                account,
                "mail/passwordResetEmail",
                "email.reset.title"
            )
        }
}