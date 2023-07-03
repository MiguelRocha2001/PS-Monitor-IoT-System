package pt.isel.iot_data_server.repository.jdbi.mappers

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceWakeUpLog
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.service.user.Role
import java.sql.Timestamp

data class UserMapper(
    val _id: String,
    val email: String,
    val role: String
)

internal fun UserMapper.toUser() = User(
    id = _id,
    userInfo = UserInfo(email, role.toRole())
)

private fun String.toRole() = when (this.uppercase()) {
    "ADMIN" -> Role.ADMIN
    "CLIENT" -> Role.CLIENT
    else -> throw IllegalArgumentException("Invalid role: $this")
}

data class DeviceMapper(
    val id: String,
    val user_id: String,
    val email: String,
    val created_at: Timestamp
)

fun DeviceMapper.toDevice() =
    Device(id, email, created_at.toInstant())

class PasswordAndSaltMapper(
    val value: String,
    val salt: String
)

class DeviceWakeUpLogMapper(
    val device_id: String,
    val timestamp: Timestamp,
    val reason: String
)

fun DeviceWakeUpLogMapper.toDeviceWakeUpLog() =
    DeviceWakeUpLog(device_id, timestamp.toInstant(), reason)