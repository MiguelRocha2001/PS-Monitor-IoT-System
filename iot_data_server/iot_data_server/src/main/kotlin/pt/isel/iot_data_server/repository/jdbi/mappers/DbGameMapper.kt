package pt.isel.iot_data_server.repository.jdbi.mappers

import pt.isel.iot_data_server.domain.User

data class UserMapper(
    val id: Int,
    val username: String,
)

internal fun UserMapper.toUser() = User(
    id = id,
    username = username,
)