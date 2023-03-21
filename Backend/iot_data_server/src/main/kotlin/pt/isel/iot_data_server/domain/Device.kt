package pt.isel.iot_data_server.domain

import java.util.*


data class DeviceId(val id: UUID)
data class Device(val deviceId: DeviceId)