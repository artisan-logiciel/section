package backend

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.allAndAwait
import org.springframework.data.r2dbc.core.awaitOneOrNull
import org.springframework.data.r2dbc.core.select
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Query.query
import reactor.kotlin.core.publisher.toMono
import java.util.*
import kotlin.test.assertEquals


fun createAccounts(accounts: Set<AccountCredentials>, repository: R2dbcEntityTemplate) {
    assertEquals(0, repository.select<AccountEntity>().count().block())
    accounts.map { repository.insert(AccountEntity(it)).block() }
    assertEquals(accounts.size.toLong(), repository.select<AccountEntity>().count().block())
}

fun deleteAccounts(repository: R2dbcEntityTemplate) {
    repository.delete(AccountEntity::class.java).toMono().block()
}

suspend fun saveAccount(model: AccountCredentials, dao: R2dbcEntityTemplate): Account? {
   return dao.insert(AccountEntity(model)).awaitSingleOrNull()?.toModel()
}

fun saveAccountAuthority(id: UUID, roleUser: String, dao: R2dbcEntityTemplate) {

}

suspend fun countAccount(dao: R2dbcEntityTemplate): Int =
    dao.select(AccountEntity::class.java).count().awaitSingle().toInt()


suspend fun countAccountAuthority(dao: R2dbcEntityTemplate): Int =
    dao.select(AccountAuthorityEntity::class.java).count().awaitSingle().toInt()


suspend fun deleteAllAccountAuthority(dao: R2dbcEntityTemplate) {
    dao.delete(AccountAuthorityEntity::class.java).allAndAwait()
}


suspend fun findOneByLogin(login: String, dao: R2dbcEntityTemplate): AccountCredentials? {
    return dao.select<AccountEntity>()
        .matching(query(where("login").`is`(login)))
        .awaitOneOrNull()?.toCredentialsModel()
}

suspend fun findOneByEmail(email: String, dao: R2dbcEntityTemplate): AccountCredentials? {
    return dao.select<AccountEntity>()
        .matching(query(where("email").`is`(email)))
        .awaitOneOrNull()?.toCredentialsModel()
}
