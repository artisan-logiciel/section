@file:Suppress("NonAsciiCharacters", "unused")

package backend

import backend.Log.log
import backend.data.Data
import backend.tdd.testLoader
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.getBean
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


internal class AccountRepositoryR2dbcTest {
    private lateinit var context: ConfigurableApplicationContext

    private val dao: R2dbcEntityTemplate by lazy { context.getBean() }
    private val accountRepository: AccountRepository by lazy { context.getBean<AccountRepositoryR2dbc>() }

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
        mono {
            val countBefore = countAccount(dao)
            assertEquals(0, countBefore)
            accountRepository.save(Data.defaultAccount)
            assertEquals(countBefore + 1, countAccount(dao))
        }
    }

    @Test
    fun test_count() = runBlocking {
        assertEquals(0, accountRepository.count())
        assertEquals(0, countAccount(dao))
        createDataAccounts(Data.accounts, dao)
        assertEquals(Data.accounts.size, countAccount(dao))
        assertEquals(Data.accounts.size.toLong(), accountRepository.count())
    }

    @Test
    fun test_delete() = runBlocking {
        assertEquals(0, countAccount(dao))
        createDataAccounts(Data.accounts, dao)
        assertEquals(Data.accounts.size, countAccount(dao))
        accountRepository.delete(Data.defaultAccount.toAccount())
        assertEquals(Data.accounts.size - 1, countAccount(dao))
    }

    @Test
    fun test_findOneByEmail() = runBlocking {
        assertEquals(0, countAccount(dao))
        createDataAccounts(Data.accounts, dao)
        assertEquals(Data.accounts.size, countAccount(dao))
        assertEquals(
            Data.defaultAccount.login,
            accountRepository.findOneByEmail(Data.defaultAccount.email!!)!!.login
        )
    }

    @Test
    fun test_findOneByLogin() = runBlocking {
        assertEquals(0, countAccount(dao))
        createDataAccounts(Data.accounts, dao)
        assertEquals(Data.accounts.size, countAccount(dao))
        assertEquals(
            Data.defaultAccount.email,
            accountRepository.findOneByLogin(Data.defaultAccount.login!!)!!.email
        )
    }

    @Test
    fun test_suppress() {
        assertEquals(0, countAccount(dao))
        createDataAccounts(Data.accounts, dao)
        assertEquals(Data.accounts.size, countAccount(dao))
        assertEquals(Data.accounts.size + 1, countAccountAuthority(dao))
        runBlocking {
            accountRepository.suppress(findOneByLogin(Data.defaultAccount.login!!, dao)!!.toAccount())
        }
        assertEquals(Data.accounts.size - 1, countAccount(dao))
        assertEquals(Data.accounts.size, countAccountAuthority(dao))
    }

    @Test
    fun test_signup() {
        assertEquals(0, countAccount(dao))
        assertEquals(0, countAccountAuthority(dao))
        runBlocking {
            accountRepository.signup(
                Data.defaultAccount.copy(
                    activationKey = RandomUtils.generateActivationKey,
                    langKey = Constants.DEFAULT_LANGUAGE,
                    createdBy = Constants.SYSTEM_USER,
                    createdDate = Instant.now(),
                    lastModifiedBy = Constants.SYSTEM_USER,
                    lastModifiedDate = Instant.now(),
                    authorities = mutableSetOf(Constants.ROLE_USER)
                )
            )
        }
        assertEquals(1, countAccount(dao))
        assertEquals(1, countAccountAuthority(dao))
    }

    @Test
    fun test_findActivationKeyByLogin() {
        assertEquals(0, countAccount(dao))
        createDataAccounts(Data.accounts, dao)
        assertEquals(Data.accounts.size, countAccount(dao))
        assertEquals(Data.accounts.size + 1, countAccountAuthority(dao))
        runBlocking {
            assertEquals(
                findOneByEmail(Data.defaultAccount.email!!, dao)!!.activationKey,
                accountRepository.findActivationKeyByLogin(Data.defaultAccount.login!!)
            )
        }
    }

    @Test
    fun test_findOneActivationKey() {
        assertEquals(0, countAccount(dao))
        createDataAccounts(Data.accounts, dao)
        assertEquals(Data.accounts.size, countAccount(dao))
        assertEquals(Data.accounts.size + 1, countAccountAuthority(dao))
        findOneByLogin(Data.defaultAccount.login!!, dao).run {
            log.info("result: $this")
            assertNotNull(this)
            assertNotNull(this.activationKey)
            runBlocking {
                accountRepository.findOneActivationKey(this@run.activationKey!!).run findOneActivationKey@{
                    log.info("findOneActivationKey: ${this@findOneActivationKey}")
//                assertEquals(
//                    result.id,
//                    accountRepository.findOneActivationKey(Data.defaultAccount.login!!)?.id
//                )
                }
            }
        }
    }
}
