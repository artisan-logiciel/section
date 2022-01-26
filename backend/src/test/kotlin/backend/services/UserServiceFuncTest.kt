package backend.services

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import backend.config.Constants
import backend.domain.DataTest
import backend.repositories.entities.Authority
import backend.repositories.entities.User
import backend.services.exceptions.EmailAlreadyUsedException
import backend.services.exceptions.UsernameAlreadyUsedException
import backend.tdd.functional.AbstractBaseFunctionalTest
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlin.test.*

/**
 * Functional tests for {@link UserService}.
 */
@Suppress("NonAsciiCharacters")
class UserServiceFuncTest : AbstractBaseFunctionalTest() {
    @Autowired
    private lateinit var userService: UserService

    @BeforeTest
    fun init() = runBlocking { deleteAllUsers() }


    @Test
    fun `test register account avec un login existant et activated est vrai, doit lancer l'exception UsernameAlreadyUsedException`(): Unit =
        runBlocking {
            saveUserWithAutorities(DataTest.defaultUser.copy(activated = true))?.apply {
                assertNotNull(id)
                assertTrue(activated)
                assertEquals(DataTest.defaultAccount.login, login)
                countUser().apply countUser@{
                    assertFailsWith<UsernameAlreadyUsedException> {
                        userService.register(DataTest.defaultAccount, DataTest.defaultAccount.password!!)
                    }
                    assertEquals(this@countUser, countUser())
                }
            }
        }

    @Test
    fun `test register account avec un email existant, un login inexistant et activated est vrai, doit lancer l'exception EmailAlreadyUsedException`(): Unit =
        runBlocking {
            saveUserWithAutorities(DataTest.defaultUser.copy(
                login = DataTest.defaultUser.login!!.reversed(),
                activated = true
            ).apply {
                assertNull(findOneUserByLogin(login!!))
            })?.apply {
                assertNotNull(id)
                assertTrue(activated)
                assertEquals(DataTest.defaultAccount.email, email)
                assertNotEquals(DataTest.defaultAccount.login, login)
                countUser().apply countUser@{
                    assertFailsWith<EmailAlreadyUsedException> {
                        userService.register(DataTest.defaultAccount, DataTest.defaultAccount.password!!)
                    }
                    assertEquals(this@countUser, countUser())
                }
            }
        }

    @Test
    fun `test register account avec un email inexistant, un login inexistant et activated est faux`(): Unit =
        runBlocking {
            countUser().apply countUserBeforeRegister@{
                countUserAuthority().apply countUserAuthorityBeforeRegister@{
                    assertNull(findOneUserByLogin(DataTest.defaultUser.login!!))
                    assertNull(findOneUserByEmail(DataTest.defaultUser.email!!))
                    assertEquals(DataTest.defaultUser.login, DataTest.defaultAccount.login)
                    assertEquals(DataTest.defaultUser.email, DataTest.defaultAccount.email)
                    assertFalse(DataTest.defaultAccount.activated)
                    assertFalse(
                        userService.register(
                            DataTest.defaultAccount,
                            DataTest.defaultAccount.password!!
                        )!!.activated
                    )
                    assertEquals(
                        countUser(),
                        this@countUserBeforeRegister + 1
                    )
                    assertEquals(
                        countUserAuthority(),
                        this@countUserAuthorityBeforeRegister + 1
                    )
                }
            }
        }

    @Test
    @WithMockUser(DataTest.USER_LOGIN)
    fun `test email non inscrit ne peut reset un password`(): Unit = runBlocking {
        checkInitDatabaseWithDefaultUser()
        assertNull(userService.requestPasswordReset("invalid.login@localhost"))
    }

    @Test
    @WithMockUser(DataTest.USER_LOGIN)
    fun `test email inscrit et activé peut reset un password`(): Unit = runBlocking {
        checkInitDatabaseWithDefaultUser()
        findOneUserByEmail(DataTest.defaultUser.email!!)!!.apply {
            assertNull(resetDate)
            assertNull(resetKey)
            assertNotNull(id)
        }
        userService.requestPasswordReset(DataTest.defaultUser.email!!).apply {
            assertNotNull(this)
            assertEquals(email, DataTest.defaultUser.email)
            assertNotNull(resetDate)
            assertNotNull(resetKey)
        }
    }

    @Test
    @WithMockUser(DataTest.USER_LOGIN)
    fun `test email inscrit et non activé ne peut pas reset un password`(): Unit = runBlocking {
        saveUserWithAutorities(DataTest.defaultUser.copy().apply {
            activated = false
        })?.apply {
            assertFalse(activated)
            assertNull(resetKey)
        }
        assertNull(userService.requestPasswordReset(DataTest.defaultUser.email!!))
        findOneUserByEmail(DataTest.defaultUser.email!!)!!.apply {
            assertNull(resetKey)
            assertFalse(activated)
        }
    }

    @Test
    @WithMockUser(DataTest.USER_LOGIN)
    fun `test resetKey d'un user activé ne doit pas avoir plus de 24 heures`(): Unit = runBlocking {
        RandomUtil.generateResetKey.apply {
            saveUserWithAutorities(
                DataTest.defaultUser.copy(
                    activated = true,
                    resetDate = Instant.now().minus(25, ChronoUnit.HOURS),
                    resetKey = this
                )
            )
            assertNotEquals(DataTest.defaultUser.password, DataTest.userTest2.password)
            assertNull(userService.completePasswordReset(DataTest.userTest2.password!!, this))
        }
    }

    @Test
    @WithMockUser(DataTest.USER_LOGIN)
    fun `test resetKey doit être valide`(): Unit = runBlocking {
        "InvalidResetKey".apply {
            saveUserWithAutorities(
                DataTest.defaultUser.copy(
                    activated = true,
                    resetDate = Instant.now().minus(25, ChronoUnit.HOURS),
                    resetKey = this
                )
            )
            assertNotEquals(DataTest.defaultUser.password, DataTest.userTest1.password)
            assertNull(userService.completePasswordReset(DataTest.userTest1.password!!, this))
        }
    }

    @Test
    @WithMockUser(DataTest.USER_LOGIN)
    fun `test un user peut reset password`(): Unit = runBlocking {
        assertEquals(countUser(), 0)
        assertEquals(countUserAuthority(), 0)
        assertTrue(DataTest.defaultUser.authorities!!.contains(Authority(Constants.ROLE_USER)))
        RandomUtil.generateResetKey.apply key@{
            saveUserWithAutorities(
                DataTest.defaultUser.copy(
                    resetDate = Instant.now().minus(2, ChronoUnit.HOURS),
                    resetKey = this
                )
            ).apply user@{
                assertNotNull(this@user)
                assertNotNull(resetDate)
                assertNotNull(resetKey)
                assertEquals(DataTest.defaultUser.password, password)
                assertTrue(this@user.authorities!!.contains(Authority(Constants.ROLE_USER)))
                userService
                    .completePasswordReset(DataTest.userTest1.password!!, this@key)
                    .apply result@{
                        assertNotNull(this@result)
                        assertNull(resetDate)
                        assertNull(resetKey)
                        assertNotEquals(DataTest.defaultUser.password!!, password)
                    }
            }
        }
        assertEquals(countUser(), 1)
        assertEquals(countUserAuthority(), 1)
    }

    @Test
    @WithMockUser(DataTest.USER_LOGIN)
    fun `test un user non activé avec une activationKey de plus de 3jours est détruit`(): Unit = runBlocking {
        assertEquals(countUser(), 0)
        assertEquals(countUserAuthority(), 0)
        assertTrue(DataTest.defaultUser.authorities!!.contains(Authority(Constants.ROLE_USER)))
        RandomUtil.generateActivationKey.apply key@{
            Instant.now().minus(4, ChronoUnit.DAYS).apply fourDaysAgo@{
                saveUserWithAutorities(
                    DataTest.defaultUser.copy(
                        activated = false,
                        createdDate = this,
                        activationKey = this@key
                    )
                ).apply user@{
                    assertEquals(countUser(), 1)
                    assertEquals(countUserAuthority(), 1)
                    assertNotNull(this)
                    assertNotNull(createdDate)
                    assertNotNull(activationKey)
                    assertEquals(DataTest.defaultUser.password, password)
                    assertTrue(authorities!!.contains(Authority(Constants.ROLE_USER)))
                    LocalDateTime.ofInstant(Instant.now().minus(3, ChronoUnit.DAYS), ZoneOffset.UTC)
                        .apply threeDaysAgo@{
                            mutableListOf<User>().apply {
                                findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
                                    this@threeDaysAgo
                                ).map { add(it) }
                                    .collect()
                                assertTrue(isNotEmpty())
                            }
                            userService.removeNotActivatedUsers()
                            mutableListOf<User>().apply {
                                findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
                                    this@threeDaysAgo
                                ).map { add(it) }
                                    .collect()
                                assertTrue(isEmpty())
                            }
                        }
                }
            }
        }
        assertEquals(countUser(), 0)
        assertEquals(countUserAuthority(), 0)
    }

    @Test
    @WithMockUser(DataTest.USER_LOGIN)
    fun `test un user créé depuis plus de 3jours et non activé avec une activationKey null n'est pas détruit`(): Unit =
        runBlocking {
            assertEquals(countUser(), 0)
            assertEquals(countUserAuthority(), 0)
            assertTrue(DataTest.defaultUser.authorities!!.contains(Authority(Constants.ROLE_USER)))
            Instant.now().minus(4, ChronoUnit.DAYS).apply {
                saveUserWithAutorities(
                    DataTest.defaultUser.copy(
                        activated = false,
                        createdDate = this
                    )
                ).apply {
                    assertNotNull(actual = this)
                    assertNotNull(actual = createdDate)
                    assertNull(actual = activationKey)
                    assertEquals(expected = DataTest.defaultUser.password, actual = password)
                    assertTrue(actual = authorities!!.contains(Authority(Constants.ROLE_USER)))
                    LocalDateTime.ofInstant(Instant.now().minus(3, ChronoUnit.DAYS), ZoneOffset.UTC)
                        .apply threeDaysAgo@{
                            mutableListOf<User>().apply {
                                findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
                                    dateTime = this@threeDaysAgo
                                ).map { add(element = it) }.collect()
                                assertTrue(isEmpty())
                            }
                        }
                }
            }
            userService.removeNotActivatedUsers()
            assertEquals(countUser(), actual = 1)
            assertEquals(countUserAuthority(), actual = 1)
        }
}