package backend.repositories.entities

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.Email as EmailConstraint

@Table("`email`")
data class Email(
    @Id val value: @EmailConstraint String
) : Persistable<String> {
    override fun getId() = value
    override fun isNew() = true
}