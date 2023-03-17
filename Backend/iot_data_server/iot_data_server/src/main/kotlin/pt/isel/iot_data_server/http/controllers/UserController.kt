package pt.isel.iot_data_server.http.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.http.CreateUserInputModel
import pt.isel.iot_data_server.service.UserService
import java.util.*

@RestController
class UserController(
    val service: UserService
) {
    @PostMapping("/token")
    fun createToken(user: User) {
        service.createToken(user.id)
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