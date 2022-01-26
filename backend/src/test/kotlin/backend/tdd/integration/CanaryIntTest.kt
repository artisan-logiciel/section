package backend.tdd.integration

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import backend.Server
import backend.config.Log.log
import kotlin.test.Test

@Suppress("unused")
class CanaryIntTest {
    private lateinit var context: ConfigurableApplicationContext

    @BeforeAll
    fun `lance le server en profile test`() {
        context = runApplication<Server> {
            testLoader(app = this)
        }
    }

    @AfterAll
    fun `arrete le serveur`() {
        context.close()
    }

    @Test
    fun `canary integration test`() = log.info("canary integration test")


    //@org.junit.jupiter.api.Disabled
    @Test
    fun `canary functional test`() = log.info("""
    ${"\n"}Bean definition names:
    ${
        context.beanDefinitionNames.apply {
            assert(contains(org.apache.commons.lang3.StringUtils.uncapitalize(Server::class.simpleName)))
        }.map { "\n" + it }.toList()
    }
    """.trimIndent()
    ).apply {
    }
}