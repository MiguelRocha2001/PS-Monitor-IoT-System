package pt.isel.iot_data_server.http.model.user

import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.service.user.Role


data class UserCreateOutputModel(val userId: String, val token: String)
data class UserEmailAlreadyRegisteredOutputModel(val exists: Boolean)

data class UsersOutputModel(val users: List<UserOutputModel>) {
    companion object {
        fun fromUsers(users: List<User>) = UsersOutputModel(users.map { it.toOutputModel() })
    }
}

data class UserOutputModel(val id: String, val username: String, val email: String, val role: Role)

fun User.toOutputModel() = UserOutputModel(id, userInfo.username, userInfo.email, userInfo.role)

data class TokenOutputModel(val token: String)

data class IsLoggedInOutputModel(val isLoggedIn: Boolean)