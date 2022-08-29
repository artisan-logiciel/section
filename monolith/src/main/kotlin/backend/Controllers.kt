package backend

import backend.domain.Account
import backend.services.AccountService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("api")
class SignUpController(
    private val accountModelService: AccountModelService
) {
    internal class AccountException(message: String) : RuntimeException(message)
    /**
     * {@code POST  /signup} : register the user.
     *
     * @param accountCredentials the managed user View Model.
     * @throws backend.services.exceptions.InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws backend.http.problems.EmailAlreadyUsedProblem {@code 400 (Bad Request)} if the email is already used.
     * @throws backend.http.problems.LoginAlreadyUsedProblem {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("signup")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun signup(
        @RequestBody @Valid accountCredentials: AccountCredentialsModel
    ) = accountModelService.signup(accountCredentials)
}


