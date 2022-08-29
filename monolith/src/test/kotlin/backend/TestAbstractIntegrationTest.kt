package backend

import backend.Data.defaultUser
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestAbstractIntegrationTest : AbstractIntegrationTest() {


    @Test
    fun `test AbstractBaseSpringBootTest_saveUserWithAuthorities`(): Unit = runBlocking {
        val countUserBeforeSave = countUser()
        assertEquals(0, countUserBeforeSave)
        val countUserAuthorityBeforeSave = countUserAuthority()
        assertEquals(0, countUserAuthorityBeforeSave)
        defaultUser.copy().apply {
            unlockUser()
            val id = saveUserWithAutorities(this)?.id
            assertEquals(countUserBeforeSave + 1, countUser())
            assertEquals(countUserAuthorityBeforeSave + 1, countUserAuthority())
            when {
                id != null -> {
                    unlockUser()
                    deleteUserByIdWithAuthorities(id)
                }
            }
        }
        assertEquals(countUserBeforeSave, countUser())
        assertEquals(countUserAuthorityBeforeSave, countUserAuthority())
    }
}