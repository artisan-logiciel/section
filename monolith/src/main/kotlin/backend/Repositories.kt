@file:Suppress("unused", "FunctionName")

package backend


import backend.Constants.ROLE_USER
import backend.Log.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.lang.Nullable
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.LocalDateTime
import java.util.*
import kotlin.NoSuchElementException

interface IAuthorityRepository {
    suspend fun findOne(role: String): String?
}

@Repository
class AuthorityRepositoryInMemory : IAuthorityRepository {
    companion object {
        private val authorities by lazy {
            mutableSetOf(
                "ADMIN",
                "USER",
                "ANONYMOUS"
            ).map { AuthorityEntity(it) }.toSet()
        }
    }

    override suspend fun findOne(role: String): String? =
        authorities.find { it.role == role }?.role

}

interface IAccountModelRepository {
    suspend fun findOneByLogin(login: String): AccountModel?

    suspend fun findOneByEmail(email: String): AccountModel?

    suspend fun save(model: AccountCredentialsModel): AccountModel?

    suspend fun delete(account: AccountModel)

    suspend fun findActivationKeyByLogin(login: String): String?

    suspend fun count(): Long
    suspend fun suppress(account: AccountModel)
    suspend fun signup(model: AccountCredentialsModel)
    suspend fun findOneActivationKey(key: String): AccountCredentialsModel?
}

@Repository
class AccountRepositoryInMemory(
    private val accountAuthorityRepository: IAccountAuthorityRepository,
    private val authorityRepository: IAuthorityRepository
) : IAccountModelRepository {

    companion object {
        private val accounts by lazy { mutableSetOf<IAccountEntity<IAuthorityEntity>>() }
    }

    override suspend fun findOneByLogin(login: String) =
        accounts.find { login.equals(it.login, ignoreCase = true) }?.toModel()

    override suspend fun findOneByEmail(email: String) =
        accounts.find { email.equals(it.email, ignoreCase = true) }?.toModel()


    fun saveCurrent(model: AccountCredentialsModel) =
        create(model).run {
            when {
                this != null -> return@run this
                else -> update(model)
            }
        }

    private fun create(model: AccountCredentialsModel) =
        if (`mail & login do not exist`(model))
            model.copy(id = UUID.randomUUID()).apply {
                @Suppress("UNCHECKED_CAST")
                accounts += AccountEntity(this) as IAccountEntity<IAuthorityEntity>
            }.toAccount() else null

    private fun `mail & login do not exist`(model: AccountCredentialsModel) =
        accounts.none {
            it.login.equals(model.login, ignoreCase = true)
                    && it.email.equals(model.email, ignoreCase = true)
        }


    private fun `mail exists and login exists`(model: AccountCredentialsModel) =
        accounts.any {
            model.email.equals(it.email, ignoreCase = true)
                    && model.login.equals(it.login, ignoreCase = true)
        }


    private fun `mail exists and login does not`(model: AccountCredentialsModel) =
        accounts.any {
            model.email.equals(it.email, ignoreCase = true)
                    && !model.login.equals(it.login, ignoreCase = true)
        }


    private fun `mail does not exist and login exists`(model: AccountCredentialsModel) =
        accounts.any {
            !model.email.equals(it.email, ignoreCase = true)
                    && model.login.equals(it.login, ignoreCase = true)
        }

    private fun update(
        model: AccountCredentialsModel,
    ): AccountModel? = when {
        `mail exists and login does not`(model) -> changeLogin(model)?.run { patch(this) }
        `mail does not exist and login exists`(model) -> changeEmail(model)?.run { patch(this) }
        `mail exists and login exists`(model) -> patch(model)
        else -> null
    }

    private fun changeLogin(
        model: AccountCredentialsModel,
    ): AccountCredentialsModel? =
        try {
            (accounts.first { model.email.equals(it.email, ignoreCase = true) } as AccountCredentialsModel).run {
                val retrieved: AccountCredentialsModel = copy(login = model.login)
                accounts.remove(this as IAccountEntity<IAuthorityEntity>?)
                (retrieved as IAccountEntity<IAuthorityEntity>?)?.run { accounts.add(this) }
                model
            }
        } catch (_: NoSuchElementException) {
            null
        }


    private fun changeEmail(
        model: AccountCredentialsModel,
    ): AccountCredentialsModel? = try {
        (accounts.first { model.login.equals(it.login, ignoreCase = true) } as AccountCredentialsModel).run {
            val retrieved: AccountCredentialsModel = copy(email = model.email)
            accounts.remove(this as IAccountEntity<IAuthorityEntity>?)
            (retrieved as IAccountEntity<IAuthorityEntity>?)?.run { accounts.add(this) }
            model
        }
    } catch (_: NoSuchElementException) {
        null
    }

    private fun patch(
        model: AccountCredentialsModel?,
    ): AccountModel? = try {
        model.run {
            val retrieved = accounts.find { this?.email?.equals(it.email, ignoreCase = true)!! }
            accounts.remove(accounts.find { this?.email?.equals(it.email, ignoreCase = true)!! })
            @Suppress("CAST_NEVER_SUCCEEDS")
            (retrieved as AccountCredentialsModel).copy(
                password = `if password is null or empty then no change`(model, retrieved),
                activationKey = `switch activationKey case then patch`(model, retrieved),
                authorities = `if password null or empty then no change`(model, retrieved)
            ).apply {
                @Suppress("CAST_NEVER_SUCCEEDS")
                accounts.add(this as IAccountEntity<IAuthorityEntity>)
            }.toAccount()
        }
    } catch (_: NoSuchElementException) {
        null
    }

    private fun `if password is null or empty then no change`(
        model: AccountCredentialsModel?,
        retrieved: AccountCredentialsModel
    ): String = when {
        model == null -> retrieved.password!!
        model.password == null -> retrieved.password!!
        model.password.isNotEmpty() -> model.password
        else -> retrieved.password!!
    }

    private fun `switch activationKey case then patch`(
        model: AccountCredentialsModel?,
        retrieved: AccountCredentialsModel
    ): String? {
        TODO("Not yet implemented")
    }

    private fun `if password null or empty then no change`(
        model: AccountCredentialsModel?,
        retrieved: AccountCredentialsModel
    ): Set<String> {
        TODO("Not yet implemented")
    }

    override suspend fun save(model: AccountCredentialsModel): AccountModel? =
        if (model.id == null && accounts.none {
                it.login?.equals(model.login, ignoreCase = true) ?: (model.login == null)
                        && it.email?.equals(model.email, ignoreCase = true) ?: (model.email == null)
            }) model
            .copy(id = UUID.randomUUID())
            .apply {
                @Suppress("UNCHECKED_CAST")
                accounts += AccountEntity(this) as IAccountEntity<IAuthorityEntity>
            }
            .toAccount()
        //TODO: les id non null sans coherence login password renvoi null
        else if (model.id != null && accounts.none {
                it.login?.equals(model.login, ignoreCase = true) ?: (model.login == null)
            } && accounts.none {
                it.email?.equals(model.email, ignoreCase = true) ?: (model.email == null)
            }) AccountEntity(model).apply {
            try {
                accounts.remove(accounts.first { this.id == it.id })
                @Suppress("UNCHECKED_CAST")
                accounts += this as IAccountEntity<IAuthorityEntity>
                log.info("accounts: $accounts")
            } catch (_: NoSuchElementException) {
            }
        }.toModel() else {
            //changer de login
            //changer d'email
            //changer firstName lastName imageUrl activated langKey
            //changer d'authorities
            model.toAccount()
        }

    override suspend fun delete(account: AccountModel) {
        accounts.apply { if (isNotEmpty()) remove(find { it.id == account.id }) }
    }

    override suspend fun findActivationKeyByLogin(login: String): String? =
        accounts.find {
            it.login.equals(login, ignoreCase = true)
        }?.activationKey

    override suspend fun count(): Long = accounts.size.toLong()
    override suspend fun suppress(account: AccountModel) {
        accountAuthorityRepository.deleteAllByAccountId(account.id!!)
        delete(account)
    }

    override suspend fun signup(model: AccountCredentialsModel) {
        accountAuthorityRepository.save(save(model)?.id!!, ROLE_USER)
    }

    override suspend fun findOneActivationKey(key: String): AccountCredentialsModel? {
        TODO("Not yet implemented")
    }
}

interface IAccountAuthorityRepository {
    suspend fun save(id: UUID, authority: String)

    suspend fun delete(id: UUID, authority: String)

    suspend fun count(): Long

    suspend fun deleteAll()

    suspend fun deleteAllByAccountId(id: UUID)
}

@Repository
class AccountAuthorityRepositoryInMemory : IAccountAuthorityRepository {
    companion object {
        private val accountAuthorities by lazy { mutableSetOf<UserAuthority>() }
    }

    override suspend fun count(): Long = accountAuthorities.size.toLong()

    override suspend fun save(id: UUID, authority: String) {
        accountAuthorities.add(UserAuthority(userId = id, role = authority))
    }


    override suspend fun delete(id: UUID, authority: String) {
        accountAuthorities.apply {
            filter { it.userId == id && it.role == authority }
                .map { remove(it) }
        }
    }

    override suspend fun deleteAll() = accountAuthorities.clear()


    override suspend fun deleteAllByAccountId(id: UUID):Unit =
        accountAuthorities.run {
            filter { it.userId == id }
                .map { remove(it) }
        }

}


interface AccountRepository {

    suspend fun findOneByLogin(login: String): Account?

    suspend fun findOneByEmail(email: String): Account?

    suspend fun save(accountCredentials: Account.AccountCredentials): Account

    suspend fun delete(account: Account)

    suspend fun findActivationKeyByLogin(login: String): String
}


@Repository
interface EmailRepository : CoroutineSortingRepository<Email, String>

interface UserRepositoryPageable : R2dbcRepository<User, UUID> {
    fun findAllByActivatedIsTrue(pageable: Pageable): Flux<User>
    fun findAllByIdNotNull(pageable: Pageable): Flux<User>
}

@Repository
class UserRepository(
    private val iUserRepository: IUserRepository,
    private val userAuthRepository: UserAuthRepository,
) {
    suspend fun saveWithoutAuth(user: User): User = iUserRepository.save(user)

    suspend fun save(user: User): User =
        saveWithoutAuth(user).apply {
            authorities.apply auths@{
                if (!isNullOrEmpty() && id != null)
                    userAuthRepository.apply {
//                        filter { findByUserIdAndRole(id!!, it.role) == null }.
                        map { saveUserAuthority(id!!, it.role) }
                            .run {
                                findAllByUserId(id!!)
                                    .filter { !this@auths.contains(Authority(it.role)) }
                                    .map { delete(it) }
                            }
                    }
            }
        }

    suspend fun count(): Long = iUserRepository.count()

    suspend fun findOneWithAuthoritiesByLogin(login: String): User? = iUserRepository
        .findOneByLogin(login)
        .apply {
            if (this != null) userAuthRepository
                .findAllByUserId(userId = id!!)
                .collect { authorities?.add(Authority(it.role)) }
        }

    suspend fun findOneWithAuthoritiesByEmail(
        email: String
    ): User? = iUserRepository
        .findOneByEmailIgnoreCase(email)
        .apply {
            if (this == null) return null
            userAuthRepository
                .findAllByUserId(this.id!!)
                .collect {
                    authorities?.add(Authority(it.role))
                }
        }

    suspend fun delete(user: User): Unit = userAuthRepository
        .deleteAllUserAuthoritiesByUser(user.id!!)
        .run {
            iUserRepository.delete(user)
        }

    suspend fun deleteAll(): Unit = userAuthRepository
        .deleteAll()
        .also { iUserRepository.deleteAll() }

    suspend fun findOneByActivationKey(activationKey: String)
            : User? = iUserRepository
        .findOneByActivationKey(activationKey)

    suspend fun findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
        dateTime: LocalDateTime
    ): Flow<User> = iUserRepository
        .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(dateTime)

    suspend fun findOneByResetKey(resetKey: String): User? =
        iUserRepository.findOneByResetKey(resetKey)


    suspend fun findOneByEmail(email: String): User? =
        iUserRepository.findOneByEmailIgnoreCase(email)

    suspend fun findOneByLogin(login: String): User? =
        iUserRepository.findOneByLogin(login)


    suspend fun findAllWithAuthorities(pageable: Pageable)
            : Flow<User> = iUserRepository
        .findAll(pageable.sort)
        .apply {
            map { u: User ->
                userAuthRepository.findAllByUserId(u.id!!).map { ua: UserAuthority ->
                    u.authorities?.add(Authority(ua.role))
                }
            }
        }
}

interface IUserRepository : CoroutineSortingRepository<User, UUID> {

    @Nullable
    @Query("SELECT * FROM `user` u WHERE lower(u.login)=lower(:login)")
    suspend fun findOneByLogin(login: String): User?

    @Nullable
    @Query("SELECT * FROM `user` WHERE activation_key = :activationKey")
    suspend fun findOneByActivationKey(activationKey: String): User?

    @Query("SELECT * FROM `user` WHERE activated = false AND activation_key IS NOT NULL AND created_date<:dateTime")
    suspend fun findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(dateTime: LocalDateTime): Flow<User>

    @Nullable
    @Query("SELECT * FROM `user` WHERE reset_key = :resetKey")
    suspend fun findOneByResetKey(resetKey: String): User?

    @Nullable
    @Query("SELECT * FROM `user` WHERE LOWER(email) = LOWER(:email)")
    suspend fun findOneByEmailIgnoreCase(email: String): User?

    @Query("SELECT COUNT(DISTINCT id) FROM `user` WHERE login != :anonymousUser")
    suspend fun countAllByLoginNot(anonymousUser: String): Long
}


interface UserAuthRepository : CoroutineCrudRepository<UserAuthority, Long> {
    @Nullable
    @Query("INSERT INTO `user_authority`(user_id,`role`) VALUES(:userId, :role)")
    suspend fun saveUserAuthority(userId: UUID, role: String): UserAuthRepository?

    @Query("DELETE FROM user_authority where user_id=:userId")
    suspend fun deleteAllUserAuthoritiesByUser(userId: UUID)

    @Query("SELECT * FROM user_authority ua where ua.user_id=:userId")
    suspend fun findAllByUserId(userId: UUID): Flow<UserAuthority>

    @Nullable
    @Query("select * from user_authority ua where ua.user_id=:userId and ua.role=:role")
    suspend fun findByUserIdAndRole(userId: UUID, role: String): UserAuthRepository?
}

@Repository
class AccountRepositoryR2dbc(
    private val userRepository: UserRepository
) : AccountRepository {

    override suspend fun findOneByLogin(login: String) =
        userRepository.findOneWithAuthoritiesByLogin(login)?.toAccount()

    override suspend fun findOneByEmail(email: String): Account? =
        userRepository.findOneByEmail(email)?.toAccount()

    override suspend fun findActivationKeyByLogin(login: String): String =
        userRepository.findOneByLogin(login)?.activationKey.toString()

    override suspend fun delete(account: Account) = account.run {
        if (login == null) return@run
        else {
            userRepository.findOneByLogin(login!!)?.apply u@{
                userRepository.delete(user = this@u)
            }.run { Log.log.debug("Changed Information for User: ${this?.login}") }
        }
    }


    override suspend fun save(accountCredentials: Account.AccountCredentials): Account {
        return (accountCredentials.login).run currentUserLogin@{
            accountCredentials.apply {
                Constants.SYSTEM_USER.apply systemUser@{
                    if (createdBy.isNullOrBlank()) {
                        createdBy = this@systemUser
                        lastModifiedBy = this@systemUser
                    } else lastModifiedBy = this@currentUserLogin
                }
                return@currentUserLogin userRepository.save(User(account = this)).toAccount()
            }
        }
    }
//    @Transactional
//    suspend fun saveUser(user: User): User = SecurityUtils.getCurrentUserLogin()
//        .run currentUserLogin@{
//            user.apply user@{
//                SYSTEM_USER.apply systemUser@{
//                    if (createdBy.isNullOrBlank()) {
//                        createdBy = this@systemUser
//                        lastModifiedBy = this@systemUser
//                    } else lastModifiedBy = this@currentUserLogin
//                }
//                userRepository.save(this@user)
//            }
//        }
}

interface AuthorityRepository : CoroutineCrudRepository<Authority, String>