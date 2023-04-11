package pt.isel.iot_data_server.http.controllers

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.http.SirenMediaType
import pt.isel.iot_data_server.http.hypermedia.createLogoutSirenAction
import pt.isel.iot_data_server.http.hypermedia.createTokenSirenAction
import pt.isel.iot_data_server.http.hypermedia.createUserSirenAction
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
                .body(siren(UserCreateOutputModel(userId, token)) {
                    clazz("users")
                    createUserSirenAction(this)
                    createTokenSirenAction(this)
                    createLogoutSirenAction(this)
                })
        }
    }

    /**
     * Since the User parameter is requested, the interceptor will try to authenticate the user.
     * If the user is not authenticated, the interceptor will throw an exception and the method will not be called.
     * Because of this, the method will only be called if the user is authenticated, and thus, the user is logically logged.
     */
    @GetMapping(Uris.NonSemantic.loggedIn)
    fun isLogged(
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
    @GetMapping(Uris.NonSemantic.logout)
    fun logout(
        user: User,
        response: HttpServletResponse
    ): ResponseEntity<Unit> {
        val cookie = buildCookie(0, null)
        response.addCookie(cookie)

        return ResponseEntity.status(204).build()
    }

    @PostMapping(Uris.Users.MY_TOKEN)
    fun createToken(
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

    private fun buildCookie(maxAge: Int, value: String?): Cookie {
        val cookieWithToken = Cookie("token", value ?: "null")
        cookieWithToken.path = "/"
        cookieWithToken.isHttpOnly = true
        cookieWithToken.secure = true
        cookieWithToken.maxAge = maxAge

        return cookieWithToken
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