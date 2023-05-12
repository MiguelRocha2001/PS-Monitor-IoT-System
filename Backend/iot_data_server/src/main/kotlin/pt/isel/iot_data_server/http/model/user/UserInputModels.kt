package pt.isel.iot_data_server.http.model.user

import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.service.user.Role

class UserCreateInputModel(username: String, password: String, val email: String) {
    val username: String
    val password: String

    init {
        this.username = username.trim()
        this.password = password.trim()
    }
}

fun UserCreateInputModel.toUserInfo(role: Role) =
    UserInfo(username, password, email, role)


class UserCreateTokenInputModel(username: String, password: String) {
    val username: String
    val password: String
    init {
        this.username = username.trim()
        this.password = password.trim()
    }
}