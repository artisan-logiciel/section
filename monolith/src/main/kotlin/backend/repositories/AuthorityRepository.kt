@file:Suppress(
    "SqlNoDataSourceInspection",
    "SqlResolve",
    "unused",
)

package backend.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import backend.repositories.entities.Authority

interface AuthorityRepository : CoroutineCrudRepository<Authority, String>