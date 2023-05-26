package pt.isel.iot_data_server.http.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.*
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.http.SirenMediaType
import pt.isel.iot_data_server.http.infra.siren
import pt.isel.iot_data_server.http.model.Problem
import pt.isel.iot_data_server.http.model.map
import pt.isel.iot_data_server.http.model.user.*
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.service.user.Role
import pt.isel.iot_data_server.service.user.UserService
import java.util.*

@Tag(name = "User", description = "User API")
@RestController
class UserController(
    val service: UserService
) {
    @Operation(summary = "Create user", description = "Create a new user")
    @ApiResponse(responseCode = "201", description = "Successfully created", content = [Content(
        mediaType = "application/vnd.siren+json",
        schema = Schema(implementation = UserCreateOutputModel::class))])
    @ApiResponse(responseCode = "400", description = "Bad request - The request was not valid", content = [Content(
        mediaType = "application/problem+json",
        schema = Schema(implementation = Problem::class))])
    @ApiResponse(responseCode = "409", description = "Conflict - The user already exists", content = [Content(
        mediaType = "application/problem+json",
        schema = Schema(implementation = Problem::class))])
    @PostMapping(Uris.Users.ALL)
    fun create(
        @RequestBody input: UserCreateInputModel
    ): ResponseEntity<*> {
        val res = service.createUser(input.email, input.password, Role.USER) // Role is always User
        return res.map {
            val userId = it.first
            val token = it.second
            ResponseEntity.status(201)
                .contentType(SirenMediaType)
                .header("Location", Uris.Users.byId(userId).toASCIIString())
                .body(siren(UserCreateOutputModel(userId, token)) { clazz("users") })
        }
    }

    @GetMapping(Uris.Users.exists.BY_EMAIL_1)
    fun isEmailAlreadyRegistered(
        @PathVariable email: String
    ): ResponseEntity<*> {
        val res = service.isEmailAlreadyRegistered(email)
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(siren(UserEmailAlreadyRegisteredOutputModel(res)) { clazz("users") })
    }

    @GetMapping(Uris.Users.ALL)
    @Authorization(Role.ADMIN)
    fun getAllUsers(
        user: User
    ): ResponseEntity<*> {
        val users = service.getAllUsers(Role.USER) // only standard users
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(
                siren(UsersOutputModel.fromUsers(users)) {
                    clazz("users")
                }
            )
    }

    @Operation(summary = "Get user", description = "Get the current user information")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = [Content(
        mediaType = "application/vnd.siren+json",
        schema = Schema(implementation = UserOutputModel::class))])
    @ApiResponse(responseCode = "401", description = "Not authorized", content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = Problem::class))])
    @GetMapping(Uris.Users.ME)
    fun getMe(
        user: User,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        val userOutputModel = UserOutputModel(
            user.id,
            user.userInfo.email,
            user.userInfo.role
        )
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(siren(userOutputModel) {
                clazz("user-me")
            })
    }

    @Operation(summary = "Authentication status", description = "Get the user authentication status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved",
       // links = [Link(name = "logout", operationId = "logout", description = "Logout the user")],
        content = [Content(
        mediaType = "application/vnd.siren+json",
        schema = Schema(implementation = IsLoggedInOutputModel::class))])
    @GetMapping(Uris.NonSemantic.loggedIn)
    fun isLoggedIn(
        request: HttpServletRequest
    ): ResponseEntity<*> {
        val token = request.cookies?.find { it.name == "token" }?.value
        val isLogged =
            if (token != null) service.isTokenValid(token)
            else false
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(siren(IsLoggedInOutputModel(isLogged)) {
                clazz("user-logged")
            })
    }

    @Operation(summary = "Get user", description = "Get the user information")
    @ApiResponse(responseCode = "204", description = "Successfully logged")
    @ApiResponse(responseCode = "403", description = "User Or Password Are Invalid", content = [Content(
        mediaType = "application/problem+json",
        schema = Schema(implementation = Problem::class))])
    @PostMapping(Uris.Users.MY_TOKEN)
    fun login(
        response: HttpServletResponse,
        @RequestBody input: UserCreateTokenInputModel
    ): ResponseEntity<*> {
        val res = service.createAndGetToken(input.email, input.password)

        // adds cookie to response
        if (res is Either.Right) {
            val age = 60 * 60 // 1 hour
            val cookieWithToken = buildCookie(age, res.value)
            response.addCookie(cookieWithToken)
        }

        return res.map {
            ResponseEntity.status(201) // Creates a new token
                .contentType(SirenMediaType)
                // location is not needed, since the token it is not allowed to fetch the token
                // instead, the user should create a new one, which will be put in the cookie
                .body(siren(TokenOutputModel(it)) { clazz("user-token") })
        }
    }

    @Operation(summary = "Login with Google", description = "Login with Google")
    @ApiResponse(responseCode = "204", description = "Successfully logged")
    @ApiResponse(responseCode = "403", description = "User Or Password Are Invalid", content = [Content(
        mediaType = "application/problem+json",
        schema = Schema(implementation = Problem::class))])
    @ApiResponse(responseCode = "409", description = "User or email already exist",
        content = [Content(
            mediaType = "application/problem+json",
            schema = Schema(implementation = Problem::class))])
    @GetMapping(Uris.GoogleAuth.GOOGLE_AUTH)
    fun loginWithGoogleAuth(
        @AuthenticationPrincipal oidcUser: OidcUser?,
        response: HttpServletResponse
    ) {
        val email = oidcUser?.userInfo?.email ?: throw RuntimeException("Error getting user info")

        val user = service.getUserByEmail(email)
        if (user == null) {
            val userCreationResult = service.createUser(email, null, Role.USER) // password is not needed
            if (userCreationResult is Either.Left) {
                throw RuntimeException("Error creating user")
            }
        }

        val res = service.createAndGetToken(email, null) // password is not needed

        // adds cookie to response
        if (res is Either.Right) {
            val cookie = buildCookie(60 * 60 * 24 * 7, res.value)
            response.addCookie(cookie)
        }

        response.sendRedirect("http://localhost:8080/devices") // FIXME
    }

    @Operation(summary = "Delete user", description = "Delete the user")
    @ApiResponse(responseCode = "204", description = "Successfully deleted")
    @ApiResponse(responseCode = "401", description = "Not authorized", content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = Problem::class))])
    @DeleteMapping(Uris.Users.BY_ID1)
    @Authorization(Role.ADMIN)
    fun deleteUser(
        @PathVariable("id") id: String
    ): ResponseEntity<Unit> {
        service.deleteUser(id)
        return ResponseEntity.status(204).build()
    }

    /**
     * Get method, because it doesn't change anything in the server.
     */
    @Operation(summary = "Logout", description = "Logout the user")
    @ApiResponse(responseCode = "204", description = "Successfully logged out")
    @DeleteMapping(Uris.NonSemantic.logout)
    fun logout(
        response: HttpServletResponse
    ): ResponseEntity<Unit> {
        val cookie = buildCookie(0, null)
        response.addCookie(cookie)

        return ResponseEntity.status(204).build()
    }

    private fun buildCookie(maxAge: Int, value: String?): Cookie {
        val cookieWithToken = Cookie("token", value ?: "null")
        cookieWithToken.path = "/"
        cookieWithToken.isHttpOnly = true
        cookieWithToken.secure = true
        cookieWithToken.maxAge = maxAge
        return cookieWithToken
    }

    /**
     * Used only for integration tests.
     * It deletes all users, except the one with the ADMIN role.
     */
    @Authorization(Role.ADMIN)
    @DeleteMapping(Uris.Users.ALL)
    fun deleteAllUsers() {
        service.deleteAllUsers(Role.USER)
    }


    //FIXME ADICIONAR DOCUMENTACAO
    @GetMapping(Uris.Verification.CODE)
    fun verifyCode(
        @RequestParam("email") email: String,
        @RequestParam("code") code: String
    ): ResponseEntity<*> {
        val res = service.codeVerification(email, code)
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(siren(UserEmailAndVerificationCodeOutputModel(res)){ clazz("users") })
    }

    // FIXME: what if an attacker keeps sending requests to this endpoint?
    @PostMapping(Uris.Verification.GENERATE)
    fun generateAndSendCodeToUserEmail(
        @RequestBody request: EmailInputModel
    ): ResponseEntity<*> {
        val code = service.generateVerificationCode(request.email)
        return ResponseEntity.status(200).
        contentType(SirenMediaType)
            .body(siren(UserCodeOutputModel(code)) { clazz("users") } )
    }
}
