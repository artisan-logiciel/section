@file:Suppress("NonAsciiCharacters", "unused")

package backend

import backend.Constants.ROLE_ADMIN
import backend.Constants.ROLE_USER
import backend.Data.ACCOUNT_LOGIN
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import kotlin.test.assertEquals

internal class AccountAuthorityRepositoryR2dbcTest {

    private lateinit var context: ConfigurableApplicationContext

    private val dao: R2dbcEntityTemplate by lazy { context.getBean() }
    private val accountAuthorityRepository: AccountAuthorityRepository by lazy { context.getBean<AccountAuthorityRepositoryR2dbc>() }

    @BeforeAll
    fun `lance le server en profile test`() = runApplication<Server> {
        testLoader(this)
    }.run { context = this }

    @AfterAll
    fun `arrête le serveur`() = context.close()


    @AfterEach
    fun tearDown() = deleteAllAccounts(dao)

    @Test
    fun test_save() {
        assertEquals(0, countAccount(dao))
        createDataAccounts(Data.accounts, dao)
        assertEquals(Data.accounts.size, countAccount(dao))
        assertEquals(Data.accounts.size + 1, countAccountAuthority(dao))
        runBlocking {
            accountAuthorityRepository.save(findOneByLogin(ACCOUNT_LOGIN, dao)!!.id!!, ROLE_ADMIN)
        }
        assertEquals(Data.accounts.size + 2, countAccountAuthority(dao))
    }

    @Test
    fun test_count() {
        runBlocking {
            assertEquals(0, accountAuthorityRepository.count())
            createDataAccounts(Data.accounts, dao)
            assertEquals(
                Data.accounts.size.toLong() + 1,
                accountAuthorityRepository.count()
            )
        }
    }

    @Test
    fun test_delete() {
        assertEquals(0, countAccount(dao))
        createDataAccounts(Data.accounts, dao)
        assertEquals(Data.accounts.size, countAccount(dao))
        assertEquals(Data.accounts.size + 1, countAccountAuthority(dao))
        runBlocking {
            accountAuthorityRepository.delete(findOneByLogin(ACCOUNT_LOGIN, dao)!!.id!!, ROLE_USER)
        }
        assertEquals(Data.accounts.size, countAccountAuthority(dao))
    }

    @Test
    fun test_deleteAll() {
        assertEquals(0, countAccount(dao))
        createDataAccounts(Data.accounts, dao)
        assertEquals(Data.accounts.size, countAccount(dao))
        assertEquals(Data.accounts.size + 1, countAccountAuthority(dao))
        runBlocking {
            accountAuthorityRepository.deleteAll()
        }
        assertEquals(Data.accounts.size, countAccount(dao))
        assertEquals(0, countAccountAuthority(dao))
    }

    @Test
    fun test_deleteAllByAccountId() {
        assertEquals(0, countAccount(dao))
        createDataAccounts(Data.accounts, dao)
        assertEquals(Data.accounts.size, countAccount(dao))
        assertEquals(Data.accounts.size + 1, countAccountAuthority(dao))
        runBlocking {
            accountAuthorityRepository.deleteAllByAccountId(findOneByLogin(ACCOUNT_LOGIN, dao)!!.id!!)
        }
        assertEquals(Data.accounts.size, countAccount(dao))
        assertEquals(Data.accounts.size, countAccountAuthority(dao))
    }
}