package pt.isel.iot_data_server.http.controllers

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.http.CreateUserInputModel
import pt.isel.iot_data_server.http.model.user.UserCreateTokenInputModel
import pt.isel.iot_data_server.service.user.UserService
import java.util.*

@RestController
class UserController(
    val service: UserService
) {
    /**
     * Since the User parameter is requested, the interceptor will try to authenticate the user.
     * If the user is not authenticated, the interceptor will throw an exception and the method will not be called.
     * Because of this, the method will only be called if the user is authenticated, and thus, the user is logically logged.
     */
    @GetMapping("/users/token")
    fun isLogged(user: User): ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/users/token")
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

    @PostMapping("/token")
    fun createToken(
        response: HttpServletResponse,
        @RequestBody input: UserCreateTokenInputModel
    ): ResponseEntity<*> {
        val res = service.createTokenAndGet(input.username, input.password)

        // adds cookie to response
        if (res is Either.Right) {
            val cookie = buildCookie(60 * 60 * 24 * 7, res.value)
            response.addCookie(cookie)
        }

        return res.map {
            ResponseEntity.status(201)
                .contentType(SirenMediaType)
                .body(
                    siren(TokenOutputModel(it)) {
                        link(Uris.Users.createToken(), Rels.SELF)
                        link(Uris.Users.home(), Rels.USER_HOME)
                        createGameSirenAction(this)
                        clazz("user-token")
                    }
                )
        }
    }

    @PostMapping("/users")
    fun createUser(
        @RequestBody userModel: CreateUserInputModel
    ) {
        service.createUser(userModel.username, userModel.password)
    }

    @GetMapping("/users")
    fun getAllUsers(): List<User> {
        return service.getAllUsers()
    }
}