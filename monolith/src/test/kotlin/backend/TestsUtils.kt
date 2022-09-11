package backend

import backend.data.Data
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.select
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue


fun createDataAccounts(accounts: Set<AccountCredentials>, dao: R2dbcEntityTemplate) {
    assertEquals(0, countAccount(dao))
    assertEquals(0, countAccountAuthority(dao))
    accounts.map { acc ->
        AccountEntity(acc.copy(
            activationKey = RandomUtils.generateActivationKey,
            langKey = Constants.DEFAULT_LANGUAGE,
            createdBy = Constants.SYSTEM_USER,
            createdDate = Instant.now(),
            lastModifiedBy = Constants.SYSTEM_USER,
            lastModifiedDate = Instant.now(),
            authorities = mutableSetOf(Constants.ROLE_USER).apply {
                if (acc.login == Data.ADMIN_LOGIN) add(Constants.ROLE_ADMIN)
            }
        )).run {
            dao.insert(this).block()!!.id!!.let { uuid ->
                authorities!!.map { authority ->
                    dao.insert(AccountAuthorityEntity(userId = uuid, role = authority.role)).block()
                }
            }
        }
    }
    assertEquals(accounts.size, countAccount(dao))
    assertTrue(accounts.size <= countAccountAuthority(dao))
}

fun deleteAllAccounts(dao: R2dbcEntityTemplate) {
    deleteAllAccountAuthority(dao)
    deleteAccounts(dao)
    assertEquals(0, countAccount(dao))
    assertEquals(0, countAccountAuthority(dao))
}

fun deleteAccounts(repository: R2dbcEntityTemplate) {
    repository.delete(AccountEntity::class.java).all().block()
}

fun deleteAllAccountAuthority(dao: R2dbcEntityTemplate) {
    dao.delete(AccountAuthorityEntity::class.java).all().block()
}

fun saveAccount(model: AccountCredentials, dao: R2dbcEntityTemplate): Account? =
    dao.insert(AccountEntity(model)).block()?.toModel()

fun saveAccountAuthority(id: UUID, role: String, dao: R2dbcEntityTemplate): AccountAuthorityEntity? =
    dao.insert(AccountAuthorityEntity(userId = id, role = role)).block()


fun countAccount(dao: R2dbcEntityTemplate): Int =
    dao.select(AccountEntity::class.java).count().block()?.toInt()!!


fun countAccountAuthority(dao: R2dbcEntityTemplate): Int =
    dao.select(AccountAuthorityEntity::class.java).count().block()?.toInt()!!


fun findOneByLogin(login: String, dao: R2dbcEntityTemplate): AccountCredentials? =
    dao.select<AccountEntity>()
        .matching(query(where("login").`is`(login)))
        .one().block()?.toCredentialsModel()

fun findOneByEmail(email: String, dao: R2dbcEntityTemplate): AccountCredentials? = dao.select<AccountEntity>()
    .matching(query(where("email").`is`(email)))
    .one().block()?.toCredentialsModel()
