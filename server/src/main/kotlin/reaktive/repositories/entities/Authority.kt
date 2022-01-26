package reaktive.repositories.entities

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


@Table("`authority`")
data class Authority(
    @Id
    @field:NotNull
    @field:Size(max = 50)
    val role: String
) : Persistable<String> {
    override fun getId() = role
    override fun isNew() = true
}