@file:Suppress(
    "SqlNoDataSourceInspection",
    "SqlResolve",
    "unused",
)

package backend.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import backend.Authority

interface AuthorityRepository : CoroutineCrudRepository<Authority, String>