package backend

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.select
import org.springframework.data.relational.core.query.Criteria.where
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

fun saveAccount(model: AccountCredentials, dao: R2dbcEntityTemplate): Account? =
    dao.insert(AccountEntity(model)).block()?.toModel()

fun saveAccountAuthority(id: UUID, role: String, dao: R2dbcEntityTemplate): AccountAuthorityEntity? =
    dao.insert(AccountAuthorityEntity(userId = id, role = role)).block()

fun countAccount(dao: R2dbcEntityTemplate): Int =
    dao.select(AccountEntity::class.java).count().block()?.toInt()!!


fun countAccountAuthority(dao: R2dbcEntityTemplate): Int =
    dao.select(AccountAuthorityEntity::class.java).count().block()?.toInt()!!


fun deleteAllAccountAuthority(dao: R2dbcEntityTemplate) {
    dao.delete(AccountAuthorityEntity::class.java).toMono().block()
}


fun findOneByLogin(login: String, dao: R2dbcEntityTemplate): AccountCredentials? =
    dao.select<AccountEntity>()
        .matching(query(where("login").`is`(login)))
        .one().block()?.toCredentialsModel()

fun findOneByEmail(email: String, dao: R2dbcEntityTemplate): AccountCredentials? = dao.select<AccountEntity>()
    .matching(query(where("email").`is`(email)))
    .one().block()?.toCredentialsModel()
