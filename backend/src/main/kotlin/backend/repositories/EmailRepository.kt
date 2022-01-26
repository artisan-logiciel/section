@file:Suppress(
    "SqlNoDataSourceInspection",
    "SqlResolve"
)

package backend.repositories

import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import backend.repositories.entities.Email

interface EmailRepository : CoroutineSortingRepository<Email, String>