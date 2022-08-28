package backend.tdd

import backend.Server.Log.log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@SpringBootTest
@ActiveProfiles("test")
internal class CanarySpringBootTest {

    @Autowired
    private lateinit var context: ApplicationContext

    @Test
    fun `canary functional test`() = log.info("canary @SpringBootTest")

    @Test
    fun contextLoads() = assert(context.beanDefinitionCount > 0)
}