package backend

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.allAndAwait
import org.springframework.data.r2dbc.core.select
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

fun saveAccount(model: AccountCredentials, dao: R2dbcEntityTemplate): Account? {
    TODO("Not yet implemented")
}

fun saveAccountAuthority(id: UUID, roleUser: String, dao: R2dbcEntityTemplate) {
    TODO("Not yet implemented")
}

suspend fun countAccount(dao: R2dbcEntityTemplate): Int =
    dao.select(AccountEntity::class.java).count().awaitSingle().toInt()


suspend fun countAccountAuthority(dao: R2dbcEntityTemplate): Int =
    dao.select(AccountAuthorityEntity::class.java).count().awaitSingle().toInt()


suspend fun deleteAllAccountAuthority(dao: R2dbcEntityTemplate) {
    dao.delete(AccountAuthorityEntity::class.java).allAndAwait()
}


suspend fun findOneByLogin(login: String): AccountCredentials? {
    TODO("Not yet implemented")
}

suspend fun findOneByEmail(email: String): AccountCredentials? {
    TODO("Not yet implemented")
}
