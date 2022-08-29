package backend

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("api")
class RegistrationController(
    val userService: UserService
) {
    /**
     * {@code POST  /register} : register the user.
     *
     * @param userCredentials the managed user View Model.
     * @throws backend.InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws backend.EmailAlreadyUsedProblem {@code 400 (Bad Request)} if the email is already used.
     * @throws backend.LoginAlreadyUsedProblem {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun register(
        @Valid
        @RequestBody
        userCredentials: UserCredentialsModel
    ) = userService.register(userCredentials)
}