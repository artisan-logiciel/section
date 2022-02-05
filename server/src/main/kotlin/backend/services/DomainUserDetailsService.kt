package backend.services

import kotlinx.coroutines.reactor.mono
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import backend.Server.Log.log
import backend.repositories.UserRepository
import backend.repositories.entities.User
import backend.services.exceptions.UserNotActivatedException
import org.springframework.security.core.userdetails.User as UserSecurity



@Component("userDetailsService")
@Suppress("unused")
class DomainUserDetailsService(
    private val userRepository: UserRepository
) : ReactiveUserDetailsService {

    @Transactional
    override fun findByUsername(login: String): Mono<UserDetails> = log
        .debug("Authenticating $login").run {
            return if (EmailValidator().isValid(login, null)) mono {
                userRepository.findOneWithAuthoritiesByEmail(login).apply {
                    if (this == null) throw UsernameNotFoundException(
                        "User with email $login was not found in the database"
                    )
                }
            }.map { createSpringSecurityUser(login, it) }
            else mono {
                userRepository.findOneWithAuthoritiesByLogin(login).apply {
                    if (this == null) throw UsernameNotFoundException(
                        "User $login was not found in the database"
                    )
                }
            }.map { createSpringSecurityUser(login, it) }
        }


    private fun createSpringSecurityUser(
        lowercaseLogin: String,
        user: User
    ): UserSecurity = if (!user.activated) {
        throw UserNotActivatedException(
            "User $lowercaseLogin was not activated"
        )
    } else UserSecurity(
        user.login!!,
        user.password!!,
        user.authorities!!.map {
            SimpleGrantedAuthority(it.role)
        }
    )
}