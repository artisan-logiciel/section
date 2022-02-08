package backend.domain

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.StringUtils.uncapitalize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import backend.Server.Log.log
import backend.domain.DataTest.defaultAccount
import backend.domain.DataTest.defaultUser
import backend.repositories.entities.User
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
class DataTestFuncTest {
    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `test account data model to json`() {
        log.info("${uncapitalize(Account::class.java.simpleName)}: ${
            objectMapper.writeValueAsString(defaultAccount).run {
                assertTrue(contains("{"))
                assertTrue(contains("}"))
                assertTrue(contains(":"))
                assertTrue(contains(","))
                assertTrue(contains("\"password\":\"${defaultAccount.password!!}\""))
                assertTrue(contains("\"login\":\"${defaultAccount.login!!}\""))
                assertTrue(contains("\"email\":\"${defaultAccount.email!!}\""))
                assertTrue(contains("\"firstName\":\"${defaultAccount.firstName!!}\""))
                assertTrue(contains("\"lastName\":\"${defaultAccount.lastName!!}\""))
                assertTrue(contains("\"langKey\":\"${defaultAccount.langKey!!}\""))
                assertTrue(contains("\"createdBy\":\"${defaultAccount.createdBy!!}\""))
                return@run this
            }
        }")
    }

    @Test
    fun `test user entity to json`() {
        log.info("${uncapitalize(User::class.java.simpleName)}: ${
            objectMapper.writeValueAsString(defaultUser).run {
                assertTrue(contains("{"))
                assertTrue(contains("}"))
                assertTrue(contains(":"))
                assertTrue(contains(","))
                assertFalse(contains("\"password\":"))
                assertTrue(contains("\"login\":\"${defaultUser.login!!}\""))
                assertTrue(contains("\"email\":\"${defaultUser.email!!}\""))
                assertTrue(contains("\"firstName\":\"${defaultUser.firstName!!}\""))
                assertTrue(contains("\"lastName\":\"${defaultUser.lastName!!}\""))
                assertTrue(contains("\"langKey\":\"${defaultUser.langKey!!}\""))
                assertFalse(contains("\"createdBy\":"))
                assertFalse(contains("\"version\":"))
                return@run this
            }
        }")
    }
}