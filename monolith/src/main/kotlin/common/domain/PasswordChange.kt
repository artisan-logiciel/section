package common.domain

data class PasswordChange(
    val currentPassword: String? = null,
    val newPassword: String? = null
)