package pt.isel.iot_data_server.http.model.user

class UserCreateInputModel(username: String, password: String) {
    val username: String
    val password: String
    init {
        this.username = username.trim()
        this.password = password.trim()
    }
}


class UserCreateTokenInputModel(username: String, password: String) {
    val username: String
    val password: String
    init {
        this.username = username.trim()
        this.password = password.trim()
    }
}