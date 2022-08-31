@file:Suppress(
    "SqlNoDataSourceInspection",
    "SqlResolve"
)

package backend

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.lang.Nullable
import java.util.*

@Suppress("unused")
interface UserAuthRepository : CoroutineCrudRepository<UserAuthority, Long> {
    @Nullable
    @Query("INSERT INTO `user_authority`(user_id,`role`) VALUES(:userId, :role)")
    suspend fun saveUserAuthority(userId: UUID, role: String): UserAuthRepository?

    @Query("DELETE FROM user_authority where user_id=:userId")
    suspend fun deleteAllUserAuthoritiesByUser(userId: UUID)

    @Query("SELECT * FROM user_authority ua where ua.user_id=:userId")
    suspend fun findAllByUserId(userId: UUID): Flow<UserAuthority>

    @Nullable
    @Query("select * from user_authority ua where ua.user_id=:userId and ua.role=:role")
    suspend fun findByUserIdAndRole(userId: UUID, role: String): UserAuthRepository?
}