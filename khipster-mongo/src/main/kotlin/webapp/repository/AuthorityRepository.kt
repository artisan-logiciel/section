package webapp.repository

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import webapp.domain.Authority

/**
 * Spring Data MongoDB repository for the [Authority] entity.
 */

interface AuthorityRepository : ReactiveMongoRepository<Authority, String>
