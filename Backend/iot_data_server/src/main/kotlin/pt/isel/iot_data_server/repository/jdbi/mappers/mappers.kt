package pt.isel.iot_data_server.repository.jdbi.mappers

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.domain.UserInfo

data class UserMapper(
    val id: Int,
    val username: String,
    val password: String,
    val email: String,
)

internal fun UserMapper.toUser() = User(
    id = id,
    userInfo = UserInfo(
        username = username,
        password = password,
        email = email,
    )
)

data class DeviceMapper(
    val id: String,
    val email: String,
)

fun DeviceMapper.toDevice() =
    Device(DeviceId(id), email)