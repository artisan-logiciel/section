package backend.repositories

import backend.domain.Account
import backend.domain.Account.AccountCredentials


interface AccountRepository {

    suspend fun findOneByLogin(login: String): Account

    suspend fun findOneByEmail(email: String): Account

    suspend fun save(accountCredentials: AccountCredentials): Account

    suspend fun delete(account: Account)

    suspend fun findActivationKeyByLogin(login: String): String
}

