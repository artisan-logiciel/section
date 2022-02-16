package backend.test

import backend.repositories.entities.Authority
import backend.repositories.entities.User
import common.config.Constants
import common.domain.Account
import org.apache.commons.lang3.StringUtils
import java.time.Instant



@Suppress("HttpUrlsUsage", "MemberVisibilityCanBePrivate")
object Datas {
    const val USER_LOGIN = "user"
    const val ADMIN_LOGIN = "admin"
    const val USER1_LOGIN = "test1"
    const val USER2_LOGIN = "test2"
    const val USER3_LOGIN = "test3"

    val defaultAccount = Account.AccountCredentials(
        password = USER_LOGIN
    ).apply {
        login = USER_LOGIN
        firstName = USER_LOGIN
        lastName = USER_LOGIN
        email = "$USER_LOGIN@acme.com"
        langKey = Constants.DEFAULT_LANGUAGE
        createdBy = Constants.SYSTEM_USER
        createdDate = Instant.now()
        lastModifiedBy = Constants.SYSTEM_USER
        lastModifiedDate = Instant.now()
        imageUrl = "http://placehold.it/50x50"
    }
    val adminAccount = Account.AccountCredentials(
        password = ADMIN_LOGIN
    ).apply {
        login = ADMIN_LOGIN
        firstName = ADMIN_LOGIN
        lastName = ADMIN_LOGIN
        email = "$ADMIN_LOGIN@acme.com"
        langKey = Constants.DEFAULT_LANGUAGE
        createdBy = Constants.SYSTEM_USER
        createdDate = Instant.now()
        lastModifiedBy = Constants.SYSTEM_USER
        lastModifiedDate = Instant.now()
    }

    val userTest1Account = Account.AccountCredentials(
        password = USER1_LOGIN
    ).apply {
        login = USER1_LOGIN
        firstName = USER1_LOGIN
        lastName = USER1_LOGIN
        email = "$USER1_LOGIN@acme.com"
        langKey = Constants.DEFAULT_LANGUAGE
        createdBy = Constants.SYSTEM_USER
        createdDate = Instant.now()
        lastModifiedBy = Constants.SYSTEM_USER
        lastModifiedDate = Instant.now()
    }

    val userTest2Account = Account.AccountCredentials(
        password = USER2_LOGIN
    ).apply {
        login = USER2_LOGIN
        firstName = USER2_LOGIN
        lastName = USER2_LOGIN
        email = "$USER2_LOGIN@acme.com"
        langKey = Constants.DEFAULT_LANGUAGE
        createdBy = Constants.SYSTEM_USER
        createdDate = Instant.now()
        lastModifiedBy = Constants.SYSTEM_USER
        lastModifiedDate = Instant.now()
    }

    val userTest3Account = Account.AccountCredentials(
        password = USER3_LOGIN
    ).apply {
        login = USER3_LOGIN
        firstName = USER3_LOGIN
        lastName = USER3_LOGIN
        email = "$USER3_LOGIN@acme.com"
        langKey = Constants.DEFAULT_LANGUAGE
        createdBy = Constants.SYSTEM_USER
        createdDate = Instant.now()
        lastModifiedBy = Constants.SYSTEM_USER
        lastModifiedDate = Instant.now()
    }

    val defaultUser = User(
        login = defaultAccount.login,
        password = defaultAccount.password,
        firstName = defaultAccount.firstName,
        lastName = defaultAccount.lastName,
        email = defaultAccount.email,
        langKey = defaultAccount.langKey,
        createdBy = defaultAccount.createdBy,
        createdDate = defaultAccount.createdDate,
        lastModifiedBy = defaultAccount.lastModifiedBy,
        lastModifiedDate = defaultAccount.lastModifiedDate,
        authorities = mutableSetOf<Authority>().apply {
            add(Authority(role = Constants.ROLE_USER))
        }
    )

    val admin = User(
        login = adminAccount.login,
        password = adminAccount.password,
        firstName = adminAccount.firstName,
        lastName = adminAccount.lastName,
        email = adminAccount.email,
        langKey = adminAccount.langKey,
        createdBy = adminAccount.createdBy,
        createdDate = adminAccount.createdDate,
        lastModifiedBy = adminAccount.lastModifiedBy,
        lastModifiedDate = adminAccount.lastModifiedDate,
        authorities = mutableSetOf<Authority>().apply {
            add(Authority(role = Constants.ROLE_USER))
            add(Authority(role = Constants.ROLE_ADMIN))
        }
    )

    val userTest1 = User(
        login = userTest1Account.login,
        password = userTest1Account.password,
        firstName = userTest1Account.firstName,
        lastName = userTest1Account.lastName,
        email = userTest1Account.email,
        langKey = Constants.DEFAULT_LANGUAGE,
        createdBy = Constants.SYSTEM_USER,
        createdDate = Instant.now(),
        lastModifiedBy = Constants.SYSTEM_USER,
        lastModifiedDate = Instant.now(),
        authorities = mutableSetOf<Authority>().apply {
            add(Authority(role = Constants.ROLE_USER))
        }
    )

    val userTest2 = User(
        login = userTest2Account.login,
        password = userTest2Account.password,
        firstName = userTest2Account.firstName,
        lastName = userTest2Account.lastName,
        email = userTest2Account.email,
        langKey = Constants.DEFAULT_LANGUAGE,
        createdBy = Constants.SYSTEM_USER,
        createdDate = Instant.now(),
        lastModifiedBy = Constants.SYSTEM_USER,
        lastModifiedDate = Instant.now(),
        authorities = mutableSetOf<Authority>().apply {
            add(Authority(role = Constants.ROLE_USER))
        }
    )

    val userTest3 = User(
        login = userTest3Account.login,
        password = userTest3Account.password,
        firstName = userTest3Account.firstName,
        lastName = userTest3Account.lastName,
        email = userTest3Account.email,
        langKey = Constants.DEFAULT_LANGUAGE,
        createdBy = Constants.SYSTEM_USER,
        createdDate = Instant.now(),
        lastModifiedBy = Constants.SYSTEM_USER,
        lastModifiedDate = Instant.now(),
        authorities = mutableSetOf<Authority>().apply {
            add(Authority(role = Constants.ROLE_USER))
        }
    )
    val users = listOf(
        admin, defaultUser, userTest1, userTest2, userTest3
    )
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

@Suppress("unused")
fun nameToLogin(userList: List<String>): List<String> = userList.map { s: String ->
    StringUtils.stripAccents(s.lowercase().replace(oldChar = ' ', newChar = '.'))
}