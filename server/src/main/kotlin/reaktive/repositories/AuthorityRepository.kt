@file:Suppress(
    "SqlNoDataSourceInspection",
    "SqlResolve"
)

package reaktive.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import reaktive.repositories.entities.Authority

interface AuthorityRepository : CoroutineCrudRepository<Authority, String>