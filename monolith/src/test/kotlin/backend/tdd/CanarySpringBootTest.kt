package backend.tdd

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import backend.Server.Log.log
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
@SpringBootTest
@ActiveProfiles("test")
class CanarySpringBootTest {

    @Autowired
    lateinit var context: ApplicationContext

    @Test
    fun `canary functional test`() = log.info("canary integration test")

    @Test
    fun contextLoads() = log.info(
        """
    ${"\n"}Bean definition names:
    ${context.beanDefinitionNames.map { "\n" + it }.toList()}
    """.trimIndent()
    ).also { assert(context.beanDefinitionNames.isNotEmpty()) }
}