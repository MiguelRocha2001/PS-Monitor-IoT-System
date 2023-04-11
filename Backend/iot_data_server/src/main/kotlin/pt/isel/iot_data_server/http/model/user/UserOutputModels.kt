package pt.isel.iot_data_server.http.model.user

import pt.isel.iot_data_server.domain.User


data class UserCreateOutputModel(val userId: Int, val token: String)

data class UsersOutputModel(val users: List<UserOutputModel>) {
    companion object {
        fun fromUsers(users: List<User>) = UsersOutputModel(users.map { it.toOutputModel() })
    }
}

data class UserOutputModel(val id: Int, val username: String, val email: String)

fun User.toOutputModel() = UserOutputModel(id, userInfo.username, userInfo.email)

data class TokenOutputModel(val token: String)

data class IsLoggedInOutputModel(val isLoggedIn: Boolean)