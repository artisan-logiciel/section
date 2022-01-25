@file:Suppress(
    "SqlNoDataSourceInspection",
    "SqlResolve"
)

package reaktive.repositories

import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import reaktive.repositories.entities.Email

interface EmailRepository : CoroutineSortingRepository<Email, String>