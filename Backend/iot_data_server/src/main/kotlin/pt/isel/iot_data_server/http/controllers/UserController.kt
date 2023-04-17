package pt.isel.iot_data_server.http.controllers

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
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
import pt.isel.iot_data_server.http.model.map
import pt.isel.iot_data_server.http.model.user.*
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.service.user.UserService
import java.util.*

@RestController
class UserController(
    val service: UserService
) {
    @ApiOperation(value = "Create User", notes = "Create a new user", response = Unit::class)
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "Successfully created"),
        ApiResponse(code = 400, message = "Bad request - The request was not understood by the server")
    ])
    @PostMapping(Uris.Users.ALL)
    fun create(
        @RequestBody @ApiParam(value = "User to create", required = true) input: UserCreateInputModel
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

    /**
     * Since the User parameter is requested, the interceptor will try to authenticate the user.
     * If the user is not authenticated, the interceptor will throw an exception and the method will not be called.
     * Because of this, the method will only be called if the user is authenticated, and thus, the user is logically logged.
     */
    @ApiOperation(value = "logged", notes = "Check if the user is logged", response = Unit::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Successfully retrieved"),
        ApiResponse(code = 400, message = "Bad request - The request was not understood by the server")
    ])
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

    /**
     * Get method, because it doesn't change anything in the server.
     */
    @ApiOperation(value = "Logout", notes = "Logout the user", response = Unit::class)
    @ApiResponses(value = [
        ApiResponse(code = 204, message = "Successfully logged out"),
        ApiResponse(code = 400, message = "Bad request - The request was not understood by the server")
    ])
    @GetMapping(Uris.NonSemantic.logout)
    fun logout(
        user: User,
        response: HttpServletResponse
    ): ResponseEntity<Unit> {
        val cookie = buildCookie(0, null)
        response.addCookie(cookie)

        return ResponseEntity.status(204).build()
    }

    @ApiOperation(value = "Get User", notes = "Get a user by id", response = UserOutputModel::class)
    @ApiResponses(value = [
        ApiResponse(code = 204, message = "Successfully created"),
        ApiResponse(code = 400, message = "Bad request - The request was not understood by the server")
    ])
    @PostMapping(Uris.Users.MY_TOKEN)
    fun login(
        response: HttpServletResponse,
        @RequestBody @ApiParam(value = "User to create", required = true) input: UserCreateTokenInputModel
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

    private fun buildCookie(maxAge: Int, value: String?): Cookie {
        val cookieWithToken = Cookie("token", value ?: "null")
        cookieWithToken.path = "/"
        cookieWithToken.isHttpOnly = true
        cookieWithToken.secure = true
        cookieWithToken.maxAge = maxAge

        return cookieWithToken
    }

    @ApiOperation(value = "Get Users", notes = "Get all users registered in our system", response = UsersOutputModel::class)
    @ApiResponses(value = [
        ApiResponse(code = 204, message = "No content"),
        ApiResponse(code = 400, message = "Bad request - The request was not understood by the server")
    ])
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

    //TODO:  NAO ENTENDO MUITO BEM COMO ESTE FUNCIONA E POR ISSO NAO VOU COMENTAR AGORA
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
}