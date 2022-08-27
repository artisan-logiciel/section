package backend.tdd

import backend.tdd.Datas.defaultUser
import kotlinx.coroutines.runBlocking
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class TestAbstractBaseSpringBootTest : AbstractBaseSpringBootTest() {

    @Ignore
    @Test
    fun `test saveUserWithAuthorities`():Unit = runBlocking {
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