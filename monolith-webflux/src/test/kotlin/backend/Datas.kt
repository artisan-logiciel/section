package backend

import org.apache.commons.lang3.StringUtils
import java.time.Instant
import kotlin.test.assertEquals


@Suppress("HttpUrlsUsage", "MemberVisibilityCanBePrivate")
object Datas {
    const val USER_LOGIN = "user"
    const val ADMIN_LOGIN = "admin"
    const val USER1_LOGIN = "test1"
    const val USER2_LOGIN = "test2"
    const val USER3_LOGIN = "test3"

    val defaultAccount = accountCredentialsFactory(USER_LOGIN)
    val adminAccount = accountCredentialsFactory(ADMIN_LOGIN)
    val userTest1Account = accountCredentialsFactory(USER1_LOGIN)
    val userTest2Account = accountCredentialsFactory(USER2_LOGIN)
    val userTest3Account = accountCredentialsFactory(USER3_LOGIN)
    val defaultUser = userFactory(defaultAccount)
    val admin = userFactory(adminAccount)
    val userTest1 = userFactory(userTest1Account)
    val userTest2 = userFactory(userTest2Account)
    val userTest3 = userFactory(userTest3Account)

    val users = setOf(defaultUser, admin, userTest1, userTest2, userTest3)

//    defaultAccount.apply {
//        assertEquals("${Datas.USER_LOGIN}@acme.com", email)
//        assertEquals(Constants.DEFAULT_LANGUAGE, langKey)
//        assertEquals(Constants.SYSTEM_USER, createdBy)
//        assertEquals(Constants.SYSTEM_USER, lastModifiedBy)
//        assertEquals("http://placehold.it/50x50", imageUrl)
//        assert(createdDate!!.isBefore(Instant.now()))
//        assert(lastModifiedDate!!.isBefore(Instant.now()))
//        setOf(login, firstName, lastName).map { assertEquals(Datas.USER_LOGIN, it) }
//    }

}


fun accountCredentialsFactory(login: String): UserCredentialsModel = UserCredentialsModel(
    password = login,
    login = login,
    firstName = login,
    lastName = login,
    email = "$login@acme.com",
    langKey = Constants.DEFAULT_LANGUAGE,
    createdBy = Constants.SYSTEM_USER,
    createdDate = Instant.now(),
    lastModifiedBy = Constants.SYSTEM_USER,
    lastModifiedDate = Instant.now(),
    imageUrl = "http://placehold.it/50x50",
)

fun userFactory(accountCredentials: UserCredentialsModel): UserEntity = UserEntity(
    login = accountCredentials.login,
    password = accountCredentials.password,
    firstName = accountCredentials.firstName,
    lastName = accountCredentials.lastName,
    email = accountCredentials.email,
    langKey = accountCredentials.langKey,
    createdBy = accountCredentials.createdBy,
    createdDate = accountCredentials.createdDate,
    lastModifiedBy = accountCredentials.lastModifiedBy,
    lastModifiedDate = accountCredentials.lastModifiedDate,
    authorities = mutableSetOf(Authority(role = Constants.ROLE_USER)).apply {
        when (accountCredentials.login) {
            Datas.adminAccount.login -> add(Authority(role = Constants.ROLE_ADMIN))
        }
    })

@Suppress("unused")
fun nameToLogin(userList: List<String>): List<String> = userList.map { s: String ->
    StringUtils.stripAccents(s.lowercase().replace(oldChar = ' ', newChar = '.'))
}

@Suppress("unused")
val writers = listOf(
    "Karl Marx",
    "Jean-Jacques Rousseau",
    "Victor Hugo",
    "Platon",
    "René Descartes",
    "Socrate",
    "Homère",
    "Paul Verlaine",
    "Claude Roy",
    "Bernard Friot",
    "François Bégaudeau",
    "Frederic Lordon",
    "Antonio Gramsci",
    "Georg Lukacs",
    "Franz Kafka",
    "Arthur Rimbaud",
    "Gérard de Nerval",
    "Paul Verlaine",
    "Dominique Pagani",
    "Rocé",
    "Chrétien de Troyes",
    "François Rabelais",
    "Montesquieu",
    "Georg Hegel",
    "Friedrich Engels",
    "Voltaire",
    "Michel Clouscard"
)

