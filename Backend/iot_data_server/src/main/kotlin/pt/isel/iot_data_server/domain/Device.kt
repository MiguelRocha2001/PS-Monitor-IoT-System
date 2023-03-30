package pt.isel.iot_data_server.domain

import pt.isel.iot_data_server.utils.trimJsonString
import java.util.*


data class DeviceId(val id: UUID)

fun fromJsonStringToDeviceId(str: String): DeviceId {
    val split = str.trimJsonString().split(",")

    val id = split
        .find { it.contains("deviceId") }
        ?.split(":")
        ?.get(1)
        ?.trim()
        ?.replace("\"", "")
        ?.toLong() ?: 0L

    return DeviceId(UUID(id, 0))
}

data class Device(val deviceId: DeviceId)