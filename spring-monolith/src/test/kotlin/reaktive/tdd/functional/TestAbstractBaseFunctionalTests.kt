package reaktive.tdd.functional

import kotlinx.coroutines.runBlocking
import reaktive.domain.DataTest.defaultUser
import reaktive.domain.unlockUser
import kotlin.test.Test
import kotlin.test.assertEquals

class TestAbstractBaseFunctionalTests : AbstractBaseFunctionalTest() {

    @Test
    fun `test saveUserWithAutorities`() = runBlocking {
        countUser().apply countUserBeforeSave@{
            countUserAuthority().apply countUserAuthorityBeforeSave@{
                defaultUser.copy().apply userTest@{
                    unlockUser()
                    saveUserWithAutorities(this@userTest)?.id.apply id@{
                        assertEquals(
                            this@countUserBeforeSave + 1,
                            countUser()
                        )
                        assertEquals(
                            this@countUserAuthorityBeforeSave + 1,
                            countUserAuthority()
                        )
                        if (this@id != null) {
                            unlockUser()
                            deleteUserByIdWithAuthorities(this@id)
                            assertEquals(
                                this@countUserBeforeSave,
                                countUser()
                            )
                            assertEquals(
                                this@countUserAuthorityBeforeSave,
                                countUserAuthority()
                            )
                        }
                    }
                }
            }
        }
    }

}