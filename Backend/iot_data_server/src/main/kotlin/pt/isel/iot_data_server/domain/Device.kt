package pt.isel.iot_data_server.domain

import pt.isel.iot_data_server.utils.trimJsonString


data class DeviceId(val id: String)

fun fromJsonStringToDeviceId(str: String): DeviceId {
    val split = str.trimJsonString().split(",")

    val id = split
        .find { it.contains("deviceId") }
        ?.split(":")
        ?.get(1)
        ?.trim()
        ?.replace("\"", "")
        ?: throw IllegalArgumentException("Invalid json string")

    return DeviceId(id)
}

data class Device(val deviceId: DeviceId, val ownerEmail: String, val ownerMobile: Long)