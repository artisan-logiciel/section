package backend

import backend.Log.log
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import kotlin.test.Test

internal class CanaryRestClientTest {
    private lateinit var context: ConfigurableApplicationContext

    @BeforeAll
    fun `lance le server en profile test`() {
        context = runApplication<Server> { testLoader(app = this) }
    }

    @AfterAll
    fun `arrete le serveur`() = context.close()

    @Test
    fun `canary integration test`() = log.info("canary rest client test")


    @Test
    fun `canary functional test`() = assert(context.beanDefinitionCount > 0)

}