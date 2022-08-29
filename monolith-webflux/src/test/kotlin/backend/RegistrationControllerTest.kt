@file:Suppress(
    "NonAsciiCharacters", "unused"//, "JUnitMalformedDeclaration"
)

package backend

//import backend.Server
//import backend.Server.Log.log
//import backend.domain.Account
//import backend.repositories.UserAuthRepository
//import backend.repositories.UserRepository
//import backend.tdd.Datas.defaultAccount
//import backend.tdd.testLoader
import backend.Constants.SPRING_PROFILE_CONF_DEFAULT_KEY
import backend.Constants.SPRING_PROFILE_TEST
import backend.Datas.defaultUserModel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.runApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals


fun testLoader(app: SpringApplication) = with(app) {
    setDefaultProperties(
        hashMapOf<String, Any>().apply {
            set(SPRING_PROFILE_CONF_DEFAULT_KEY, SPRING_PROFILE_TEST)
        })
    setAdditionalProfiles(SPRING_PROFILE_TEST)
}

@SpringBootTest
@ActiveProfiles("test")
class RegistrationControllerTest {

//    private lateinit var context: ConfigurableApplicationContext
//    private val client: WebTestClient by lazy {
//        WebTestClient
//            .bindToServer()
//            .baseUrl("http://localhost:8080")
//            .build()
//    }
//    @BeforeAll
//    fun `lance le server en profile test`() =
//        runApplication<MonolithWebfluxApplication> { testLoader(app = this) }
//            .run { context = this }
//    @AfterAll
//    fun `arrête le serveur`() = context.close()

    @Autowired
    private lateinit var client: WebTestClient

    //    @Ignore
    @Test
    @Ignore
    fun `register user`() = runBlocking {
        //TODO: compter les user_auth comme avec user
        //        log.info("count 1 user authorities: ${context.getBean<UserAuthRepository>().count()}")
        //        log.info("count 2 user authorities: ${context.getBean<UserAuthRepository>().count()}")
//        val countUserBefore = context.getBean<UserRepository>().count()
//        val countUserAuthBefore = context.getBean<UserAuthRepository>().count()
//        assertEquals(0, countUserBefore)
//        assertEquals(0, countUserAuthBefore)
        client
            .post()
            .uri("/api/register")
            .bodyValue(defaultUserModel)
            .exchange()
            .returnResult<Unit>().apply {
                assert(requestBodyContent!!.isNotEmpty())
                requestBodyContent
                    ?.map { it.toInt().toChar().toString() }
                    ?.reduce { acc: String, s: String -> acc + s }.apply requestContent@{
                        //test request contains passed values
                        defaultUserModel.run {
                            setOf(
                                "\"login\":\"${login}\"",
                                "\"password\":\"${password}\"",
                                "\"firstName\":\"${firstName}\"",
                                "\"lastName\":\"${lastName}\"",
                                "\"email\":\"${email}\"",
                                "\"imageUrl\":\"${imageUrl}\""
                            ).map { assert(this@requestContent?.contains(it) ?: false) }
                        }
                    }
                responseBodyContent?.isEmpty()?.let { assert(it) }
//                assertEquals(expected = HttpStatus.CREATED, actual = status)
            }
//        assertEquals(countUserBefore + 1, context.getBean<UserRepository>().count())
//        assertEquals(countUserAuthBefore + 1, context.getBean<UserAuthRepository>().count())
        //clean after test
//        context.getBean<UserAuthRepository>().deleteAll()
//        context.getBean<UserRepository>().deleteAll()
//        assertEquals(countUserAuthBefore, context.getBean<UserAuthRepository>().count())
//        assertEquals(countUserBefore, context.getBean<UserRepository>().count())
    }

    //TODO: register un user avec un email invalid
    //TODO: register un user avec un email existant
    //TODO: register un user avec un mauvais login
    //TODO: register un user avec un login existant
    //TODO: mocker que l'email est parti en interceptant l'appel et logger l'action(en affichant le mail)


}