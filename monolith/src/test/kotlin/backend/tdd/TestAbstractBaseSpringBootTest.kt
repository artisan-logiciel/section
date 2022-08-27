package backend.tdd

import backend.Server.Log.log
import backend.tdd.Datas.defaultUser
import kotlinx.coroutines.runBlocking
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class TestAbstractBaseSpringBootTest : AbstractBaseSpringBootTest() {

    @Test
    fun `test AbstractBaseSpringBootTest_saveUserWithAuthorities`():Unit = runBlocking {
        countUser().apply countUserBeforeSave@{
            assertEquals(0,this@countUserBeforeSave)
            countUserAuthority().apply countUserAuthorityBeforeSave@{
                assertEquals(0,this@countUserAuthorityBeforeSave)
                defaultUser.copy().apply userTest@{
                    unlockUser()
                    saveUserWithAutorities(this@userTest)?.id.apply id@{
                        assertEquals(this@countUserBeforeSave + 1, countUser())
                        assertEquals(this@countUserAuthorityBeforeSave + 1, countUserAuthority())
                        if (this@id != null) {
                            unlockUser()
                            deleteUserByIdWithAuthorities(this@id)
                            assertEquals(this@countUserBeforeSave, countUser())
                            assertEquals(this@countUserAuthorityBeforeSave, countUserAuthority())
                        }
                    }
                }
            }
        }
    }

}