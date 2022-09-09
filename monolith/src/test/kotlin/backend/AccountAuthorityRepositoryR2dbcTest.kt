package backend

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import java.util.*


//@Repository
//class AccountAuthorityRepositoryR2dbc(
//    private val repository: R2dbcEntityTemplate
//) : AccountAuthorityRepository {
//    //    override suspend fun findOne(role: String): String? =
////        repository.select(AuthorityEntity::class.java)
////            .matching(Query.query(Criteria.where(ROLE_COLUMN).`is`(role)))
////            .awaitOneOrNull()?.role
//    override suspend fun save(id: UUID, authority: String) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun delete(id: UUID, authority: String) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun count(): Long {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun deleteAll() {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun deleteAllByAccountId(id: UUID) {
//        TODO("Not yet implemented")
//    }
//}

internal class AccountAuthorityRepositoryR2dbcTest {

    @Test
    fun save() {
    }

    @Test
    fun delete() {
    }

    @Test
    fun count() {
    }

    @Test
    fun deleteAll() {
    }

    @Test
    fun deleteAllByAccountId() {
    }
}