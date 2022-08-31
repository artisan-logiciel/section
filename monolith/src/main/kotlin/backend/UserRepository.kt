@file:Suppress(
    "SqlNoDataSourceInspection",
    "SqlResolve",
    "unused"
)

package backend

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository("userRepository")
class UserRepository(
    private val iUserRepository: IUserRepository,
    private val userAuthRepository: UserAuthRepository,
) {
    suspend fun saveWithoutAuth(user: User): User = iUserRepository.save(user)

    suspend fun save(user: User): User =
        saveWithoutAuth(user).apply {
            authorities.apply auths@{
                if (!isNullOrEmpty() && id != null)
                    userAuthRepository.apply {
//                        filter { findByUserIdAndRole(id!!, it.role) == null }.
                        map { saveUserAuthority(id!!, it.role) }
                            .run {
                                findAllByUserId(id!!)
                                    .filter { !this@auths.contains(Authority(it.role)) }
                                    .map { delete(it) }
                            }
                    }
            }
        }

    suspend fun count(): Long = iUserRepository.count()

    suspend fun findOneWithAuthoritiesByLogin(login: String): User? = iUserRepository
        .findOneByLogin(login)
        .apply {
            if (this != null) userAuthRepository
                .findAllByUserId(userId = id!!)
                .collect { authorities?.add(Authority(it.role)) }
        }

    suspend fun findOneWithAuthoritiesByEmail(
        email: String
    ): User? = iUserRepository
        .findOneByEmailIgnoreCase(email)
        .apply {
            if (this == null) return null
            userAuthRepository
                .findAllByUserId(this.id!!)
                .collect {
                    authorities?.add(Authority(it.role))
                }
        }

    suspend fun delete(user: User): Unit = userAuthRepository
        .deleteAllUserAuthoritiesByUser(user.id!!)
        .run {
            iUserRepository.delete(user)
        }

    suspend fun deleteAll(): Unit = userAuthRepository
        .deleteAll()
        .also { iUserRepository.deleteAll() }

    suspend fun findOneByActivationKey(activationKey: String)
            : User? = iUserRepository
        .findOneByActivationKey(activationKey)

    suspend fun findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
        dateTime: LocalDateTime
    ): Flow<User> = iUserRepository
        .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(dateTime)

    suspend fun findOneByResetKey(resetKey: String): User? =
        iUserRepository.findOneByResetKey(resetKey)


    suspend fun findOneByEmail(email: String): User? =
        iUserRepository.findOneByEmailIgnoreCase(email)

    suspend fun findOneByLogin(login: String): User? =
        iUserRepository.findOneByLogin(login)


    suspend fun findAllWithAuthorities(pageable: Pageable)
            : Flow<User> = iUserRepository
        .findAll(pageable.sort)
        .apply {
            map { u: User ->
                userAuthRepository.findAllByUserId(u.id!!).map { ua: UserAuthority ->
                    u.authorities?.add(Authority(ua.role))
                }
            }
        }
}