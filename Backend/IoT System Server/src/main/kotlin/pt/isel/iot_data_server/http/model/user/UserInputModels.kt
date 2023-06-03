package pt.isel.iot_data_server.http.model.user

class UserCreateInputModel(val email: String, password: String) {
    val password: String

    init {
        this.password = password.trim()
    }
}


class UserCreateTokenInputModel(email: String, password: String) {
    val email: String
    val password: String
    init {
        this.email = email.trim()
        this.password = password.trim()
    }

}

data class EmailInputModel(val email: String)