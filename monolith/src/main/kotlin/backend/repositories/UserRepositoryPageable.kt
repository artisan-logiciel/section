@file:Suppress(
    "SqlNoDataSourceInspection",
    "SqlResolve",
    "unused"
)

package backend.repositories

import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import backend.User
import java.util.*

interface UserRepositoryPageable : R2dbcRepository<User, UUID> {
    fun findAllByActivatedIsTrue(pageable: Pageable): Flux<User>
    fun findAllByIdNotNull(pageable: Pageable): Flux<User>
}