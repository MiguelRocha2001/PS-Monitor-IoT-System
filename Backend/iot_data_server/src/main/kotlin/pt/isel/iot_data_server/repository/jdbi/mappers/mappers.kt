package pt.isel.iot_data_server.repository.jdbi.mappers

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.domain.UserInfo
import java.util.*

data class UserMapper(
    val id: Int,
    val username: String,
    val password: String,
    val email: String,
    val mobile: String
)

internal fun UserMapper.toUser() = User(
    id = id,
    userInfo = UserInfo(
        username = username,
        password = password,
        email = email,
        mobile = mobile
    )
)

data class DeviceMapper(
    val id: String,
    val email: String,
    val mobile: Long
)

fun DeviceMapper.toDevice() =
    Device(DeviceId(id), email, mobile)