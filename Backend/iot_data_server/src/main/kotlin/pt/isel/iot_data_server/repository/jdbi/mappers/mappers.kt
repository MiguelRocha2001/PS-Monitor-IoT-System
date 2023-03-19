package pt.isel.iot_data_server.repository.jdbi.mappers

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.User
import java.util.*

data class UserMapper(
    val id: Int,
    val username: String,
    val password: String,
)

internal fun UserMapper.toUser() = User(
    id = id,
    username = username,
    password = password,
)

data class DeviceMapper(
    val id: String,
)

fun DeviceMapper.toDevice() =
    Device(DeviceId(UUID.fromString(id)))