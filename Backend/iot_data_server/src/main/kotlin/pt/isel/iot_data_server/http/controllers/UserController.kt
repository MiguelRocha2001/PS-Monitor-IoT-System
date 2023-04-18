package pt.isel.iot_data_server.http.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.links.Link
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
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.http.SirenMediaType
import pt.isel.iot_data_server.http.infra.siren
import pt.isel.iot_data_server.http.model.Problem
import pt.isel.iot_data_server.http.model.map
import pt.isel.iot_data_server.http.model.user.*
import pt.isel.iot_data_server.service.Either
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
        val res = service.createUser(input.toUserInfo())
        return res.map {
            val userId = it.first
            val token = it.second
            ResponseEntity.status(201)
                .contentType(SirenMediaType)
                .header("Location", Uris.Users.byId(userId).toASCIIString())
                .body(siren(UserCreateOutputModel(userId, token)) { clazz("users") })
        }
    }

    @GetMapping(Uris.Users.ALL)
    fun getAllUsers(): ResponseEntity<*> {
        val users = service.getAllUsers()
        return if (users.isEmpty())
            ResponseEntity.status(204).build<Unit>()
        else
            ResponseEntity.status(200)
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
        user: User
    ): ResponseEntity<*> {
        val userOutputModel = UserOutputModel(
            user.id,
            user.userInfo.username,
            user.userInfo.email,
        )
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(siren(userOutputModel) {
                clazz("user-me")
            })
    }

    /**
     * Since the User parameter is requested, the interceptor will try to authenticate the user.
     * If the user is not authenticated, the interceptor will throw an exception and the method will not be called.
     * Because of this, the method will only be called if the user is authenticated, and thus, the user is logically logged.
     */
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
        val isLogged = request.cookies?.find { it.name == "token" } != null
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
        val res = service.createAndGetToken(input.username)

        // adds cookie to response
        if (res is Either.Right) {
            val cookie = buildCookie(60 * 60 * 24 * 7, res.value)
            response.addCookie(cookie)
        }

        return res.map {
            ResponseEntity.status(204) // no content
                .contentType(SirenMediaType)
                // location is not needed, since the token it is not allowed to fetch the token
                // instead, the user should create a new one, which will be put in the cookie
                .build<Unit>()
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
        val email = oidcUser?.userInfo?.email
        val username = email?.substringBefore('@')

        if (email == null || username == null) {
            throw RuntimeException("Error getting user info")
        }

        val password = "Static=password1"

        val user = service.getUserByEmailAddress(email)
        if (user == null) {
            val userCreationResult = service.createUser(UserInfo(username, password, email))
            if (userCreationResult is Either.Left) {
                throw RuntimeException("Error creating user")
            }
        }

        val res = service.createAndGetToken(username)

        // adds cookie to response
        if (res is Either.Right) {
            val cookie = buildCookie(60 * 60 * 24 * 7, res.value)
            response.addCookie(cookie)
        }

        response.sendRedirect("http://localhost:8080/auth/login")
    }

        /**
     * Get method, because it doesn't change anything in the server.
     */
    @Operation(summary = "Logout", description = "Logout the user")
    @ApiResponse(responseCode = "204", description = "Successfully logged out")
    @GetMapping(Uris.NonSemantic.logout)
    fun logout(
        user: User,
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
}