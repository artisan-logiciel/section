@file:Suppress(
    "SqlNoDataSourceInspection",
    "SqlResolve"
)

package backend.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.lang.Nullable
import backend.repositories.entities.User
import java.time.LocalDateTime
import java.util.*

interface IUserRepository : CoroutineSortingRepository<User, UUID> {

    @Nullable
    @Query("SELECT * FROM `user` u WHERE lower(u.login)=lower(:login)")
    suspend fun findOneByLogin(login: String): User?

    @Nullable
    @Query("SELECT * FROM user WHERE activation_key = :activationKey")
    suspend fun findOneByActivationKey(activationKey: String): User?

    @Query("SELECT * FROM user WHERE activated = false AND activation_key IS NOT NULL AND created_date<:dateTime")
    suspend fun findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(dateTime: LocalDateTime): Flow<User>

    @Nullable
    @Query("SELECT * FROM user WHERE reset_key = :resetKey")
    suspend fun findOneByResetKey(resetKey: String): User?

    @Nullable
    @Query("SELECT * FROM user WHERE LOWER(email) = LOWER(:email)")
    suspend fun findOneByEmailIgnoreCase(email: String): User?

    @Query("SELECT COUNT(DISTINCT id) FROM user WHERE login != :anonymousUser")
    suspend fun countAllByLoginNot(anonymousUser: String): Long
}