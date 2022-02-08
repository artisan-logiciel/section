@file:Suppress(
    "SqlNoDataSourceInspection",
    "SqlResolve"
)

package backend.repositories

import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import backend.repositories.entities.Email
import org.springframework.stereotype.Repository

@Repository("emailRepository")
@Suppress("unused")
interface EmailRepository : CoroutineSortingRepository<Email, String>