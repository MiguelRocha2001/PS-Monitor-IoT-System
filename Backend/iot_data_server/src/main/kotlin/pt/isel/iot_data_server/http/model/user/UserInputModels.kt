package pt.isel.iot_data_server.http.model.user

import pt.isel.iot_data_server.domain.UserInfo
import java.util.regex.Pattern

class UserCreateInputModel(username: String, password: String, val email: String, mobile: String) {
    val username: String
    val password: String
    val mobile: String

    init {
        this.username = username.trim()
        this.password = password.trim()
        this.mobile = mobile.trim()
        mobile.forEach { c ->
            if (!c.isDigit()) {
                throw IllegalArgumentException("Mobile number must contain only digits")
            }
        }
    }
}

fun UserCreateInputModel.toUserInfo() = UserInfo(username, password, email, mobile)


class UserCreateTokenInputModel(username: String, password: String) {
    val username: String
    val password: String
    init {
        this.username = username.trim()
        this.password = password.trim()
    }
}