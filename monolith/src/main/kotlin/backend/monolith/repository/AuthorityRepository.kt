package backend.monolith.repository

import backend.monolith.domain.Authority
import org.springframework.data.r2dbc.repository.R2dbcRepository

/**
 * Spring Data R2DBC repository for the [Authority] entity.
 */

interface AuthorityRepository : R2dbcRepository<Authority, String>
