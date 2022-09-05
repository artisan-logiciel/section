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

    override suspend fun findOneByLogin(login: String): AccountModel? =
        accounts.find { it.login?.lowercase() == login.lowercase() }?.toModel()

    override suspend fun findOneByEmail(email: String): AccountModel? =
        accounts.find { it.email?.lowercase().equals(email.lowercase()) }?.toModel()


    fun saveCurrent(model: AccountCredentialsModel): AccountModel? =
        create(model, accounts).run {
            when {
                this != null -> return@run this
                else -> update(model, accounts)
            }
        }

    private fun create(
        model: AccountCredentialsModel,
        accounts: MutableSet<IAccountEntity<IAuthorityEntity>>
    ): AccountModel? {
        return if (`mail & login do not exist`(model, accounts)) {
            model
                .copy(id = UUID.randomUUID())
                .apply {
                    @Suppress("UNCHECKED_CAST")
                    accounts += AccountEntity(this) as IAccountEntity<IAuthorityEntity>
                }.toAccount()
        } else null
    }

    private fun `mail & login do not exist`(
        model: AccountCredentialsModel,
        accounts: MutableSet<IAccountEntity<IAuthorityEntity>>
    ): Boolean = accounts.none {
        it.login.equals(model.login, ignoreCase = true)
                && it.email.equals(model.email, ignoreCase = true)
    }


    private fun `mail exists and login exists`(
        model: AccountCredentialsModel,
        accounts: MutableSet<IAccountEntity<IAuthorityEntity>>
    ): Boolean = @Suppress("SimplifiableCallChain")
    accounts.filter {
        model.email.equals(it.email, ignoreCase = true)
                && model.login.equals(it.login, ignoreCase = true)
    }.isEmpty()


    private fun `mail exists and login does not`(
        model: AccountCredentialsModel,
        accounts: MutableSet<IAccountEntity<IAuthorityEntity>>
    ) {

    }

    private fun `mail does not exist and login exists`(
        model: AccountCredentialsModel,
        accounts: MutableSet<IAccountEntity<IAuthorityEntity>>
    ) {

    }

    private fun update(
        model: AccountCredentialsModel,
        accounts: MutableSet<IAccountEntity<IAuthorityEntity>>
    ): AccountModel? {
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

    fun `foo`() {}
    override suspend fun delete(account: AccountModel) {
        accounts.apply { if (isNotEmpty()) remove(find { it.id == account.id }) }
    }

    override suspend fun findActivationKeyByLogin(login: String): String? =
        accounts.find {
            it.login?.lowercase().equals(login.lowercase())
        }?.activationKey

    override suspend fun count(): Long = accounts.size.toLong()
    override suspend fun suppress(account: AccountModel) {
        accountAuthorityRepository.deleteAllByAccountId(account.id!!)
        delete(account)
    }

    override suspend fun signup(model: AccountCredentialsModel) {
        accountAuthorityRepository.save(save(model)?.id!!, ROLE_USER)
//        save(model)
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


    override suspend fun deleteAllByAccountId(id: UUID) {
        accountAuthorities.apply {
            filter { it.userId == id }
                .map { remove(it) }
        }
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