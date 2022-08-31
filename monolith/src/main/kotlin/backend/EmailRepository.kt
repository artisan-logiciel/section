@file:Suppress(
    "SqlNoDataSourceInspection",
    "SqlResolve",
    "unused",
)

package backend

import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import backend.Email
import org.springframework.stereotype.Repository

@Repository("emailRepository")
interface EmailRepository : CoroutineSortingRepository<Email, String>