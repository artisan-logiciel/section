@file:Suppress(
    "SqlNoDataSourceInspection",
    "SqlResolve",
    "unused",
)

package backend

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import backend.Authority

interface AuthorityRepository : CoroutineCrudRepository<Authority, String>