package backend.canaries

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@SpringBootTest
@ActiveProfiles("test")
internal class CanaryIntegrationTest {

    @Autowired
    private lateinit var context: ApplicationContext

    @Test
    fun contextLoads() = assert(context.beanDefinitionCount > 0)
}