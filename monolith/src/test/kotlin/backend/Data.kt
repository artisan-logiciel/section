@file:Suppress(
    "unused",
    "HttpUrlsUsage",
    "MemberVisibilityCanBePrivate"
)

package backend

import backend.Constants.ADMIN
import backend.Constants.USER
import org.apache.commons.lang3.StringUtils


internal object Data {

    val adminAccount by lazy { accountCredentialsFactory(ADMIN) }
    val defaultAccount by lazy { accountCredentialsFactory(USER) }

    val accounts = setOf(adminAccount, defaultAccount)
}


fun accountCredentialsFactory(login: String): AccountCredentials =
    AccountCredentials(
        password = login,
        login = login,
        firstName = login,
        lastName = login,
        email = "$login@acme.com",
        imageUrl = "http://placehold.it/50x50",
    )

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

