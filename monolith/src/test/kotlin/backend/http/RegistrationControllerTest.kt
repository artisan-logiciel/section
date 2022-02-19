package backend.http

//import backend.repositories.AccountRepository
//import backend.services.RandomUtils.generateActivationKey
//import backend.test.Datas.defaultAccount
//import common.domain.Account
//import io.mockk.coEvery
//import io.mockk.mockk
//import kotlinx.coroutines.runBlocking
//import org.junit.jupiter.api.extension.ExtendWith
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
//import org.springframework.context.ConfigurableApplicationContext
//import org.springframework.http.HttpStatus.CREATED
//import org.springframework.test.context.ActiveProfiles
//import org.springframework.test.context.junit.jupiter.SpringExtension
//import org.springframework.test.web.reactive.server.WebTestClient
//import org.springframework.test.web.reactive.server.returnResult
//import reactor.kotlin.core.publisher.toMono
//import kotlin.test.Ignore
//import kotlin.test.Test
//import kotlin.test.assertEquals
//
//@Ignore
//@WebMvcTest
//@ActiveProfiles("test")
//@ExtendWith(SpringExtension::class)
//class RegistrationControllerTest {
//
//    @Autowired
//    lateinit var context: ConfigurableApplicationContext
//
//    private val client: WebTestClient by lazy {
//        WebTestClient
//            .bindToServer()
//            .baseUrl("http://localhost:8080")
//            .build()
//    }
//
////    @BeforeAll
////    @Suppress("unused")
////    fun `lance le server en profile test`() {
////        runApplication<Server> {
////            testLoader(app = this)
////        }.apply { context = this }
////    }
//
////    @AfterAll
////    @Suppress("unused")
////    fun `arrête le serveur`() = context.close()
//
//    @Test
//    fun `register user`() = runBlocking {
//
////        val userRepository = mockk<UserRepository>(relaxed = true)
////        val authorityRepository = mockk<AuthorityRepository>(relaxed = true)
////        val userAuthorityRepository = mockk<UserAuthRepository>(relaxed = true)
//        val accountRepository = mockk<AccountRepository>(relaxed = true)
//
//        coEvery {
//            accountRepository.findOneByEmail(defaultAccount.email!!)
//        } returns defaultAccount
//
//        coEvery {
//            accountRepository.findActivationKeyByLogin(defaultAccount.login!!)
//        } returns generateActivationKey
//
//        coEvery {
//            accountRepository.delete(defaultAccount)
//        } returns Unit
//
//        assertEquals(
//            expected = CREATED,
//            actual = client.post().uri("/api/register")
//                .bodyValue(defaultAccount)
//                .exchange()
//                .returnResult<Account>()
//                .status.toMono().block()
//        )
//
////        coVerify {
////            accountRepository.findOneByEmail(email = defaultAccount.email!!)
////            accountRepository.findActivationKeyByLogin(login = defaultAccount.login!!)
////            accountRepository.delete(account = defaultAccount)
////        }
//    }
//    //TODO: faire un mock de UserRepository, AuthorityRepository, UserAuthRepository
//}
