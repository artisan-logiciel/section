@file:Suppress(
    "unused",
    "HttpUrlsUsage",
    "MemberVisibilityCanBePrivate"
)

package backend

import org.apache.commons.lang3.StringUtils


internal object Data {
    const val ADMIN_LOGIN = "admin"
    const val ACCOUNT_LOGIN = "user"
    const val ACCOUNT1_LOGIN = "test1"
    const val ACCOUNT2_LOGIN = "test2"
    const val ACCOUNT3_LOGIN = "test3"

    val adminAccount by lazy { accountCredentialsFactory(ADMIN_LOGIN) }
    val defaultAccount by lazy { accountCredentialsFactory(ACCOUNT_LOGIN) }
    val account1 by lazy { accountCredentialsFactory(ACCOUNT1_LOGIN) }
    val account2 by lazy { accountCredentialsFactory(ACCOUNT2_LOGIN) }
    val account3 by lazy { accountCredentialsFactory(ACCOUNT3_LOGIN) }

    val accounts = setOf(adminAccount,defaultAccount,  account1, account2, account3)
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

