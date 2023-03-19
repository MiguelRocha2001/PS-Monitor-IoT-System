package pt.isel.iot_data_server.http.model.user


data class UserCreateOutputModel(val userId: Int, val token: String)

data class TokenOutputModel(val token: String)
