package pt.isel.iot_data_server.http.model.user

import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.service.user.Role

class UserCreateInputModel(username: String, password: String, val email: String, val role: String) {
    val username: String
    val password: String

    init {
        this.username = username.trim()
        this.password = password.trim()
    }
}

fun String.toRole() =
    when {
        this == "admin" -> Role.ADMIN
        this == "user" -> Role.USER
        else -> throw IllegalArgumentException("Invalid role")
    }

fun UserCreateInputModel.toUserInfo() =
    UserInfo(username, password, email, role.toRole())


class UserCreateTokenInputModel(username: String, password: String) {
    val username: String
    val password: String
    init {
        this.username = username.trim()
        this.password = password.trim()
    }
}