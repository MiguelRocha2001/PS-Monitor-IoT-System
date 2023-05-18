package pt.isel.iot_data_server.http.model.user

import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.service.user.Role

class UserCreateInputModel(val email: String, password: String) {
    val password: String

    init {
        this.password = password.trim()
    }
}

fun UserCreateInputModel.toUserInfo(role: Role) =
    UserInfo(email, password, role)


class UserCreateTokenInputModel(email: String, password: String) {
    val email: String
    val password: String
    init {
        this.email = email.trim()
        this.password = password.trim()
    }

}

data class EmailInputModel(val email: String)