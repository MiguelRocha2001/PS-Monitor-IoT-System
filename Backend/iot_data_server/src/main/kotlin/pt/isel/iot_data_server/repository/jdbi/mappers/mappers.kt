package pt.isel.iot_data_server.repository.jdbi.mappers

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.http.model.user.toRole

data class UserMapper(
    val _id: String,
    val username: String,
    val password: String,
    val email: String,
    val role: String
)

internal fun UserMapper.toUser() = User(
    id = _id,
    userInfo = UserInfo(username, password, email, role.toRole())
)

data class DeviceMapper(
    val id: String,
    val user_id: String,
    val email: String,
)

fun DeviceMapper.toDevice() =
    Device(id, email)