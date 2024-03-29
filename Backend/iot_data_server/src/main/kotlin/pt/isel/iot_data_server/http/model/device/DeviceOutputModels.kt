package pt.isel.iot_data_server.http.model.device

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceWakeUpLog

data class DeviceIdOutputModel(val id: String)

data class DeviceCountOutputModel(val deviceCount: Int)

data class DevicesOutputModel(val devices: List<DeviceOutputModel>) {

    companion object {
        fun from(devices: List<Device>) = DevicesOutputModel(devices.map { it.toDeviceOutputModel() })
    }
}
fun Device.toDeviceOutputModel() = DeviceOutputModel(id = this.deviceId, email = this.ownerEmail)

data class DeviceOutputModel(val id: String, val email: String)

data class CreateDeviceOutputModel(val deviceId: String)

data class DeviceWakeUpLogOutputModel(
    val deviceId: String,
    val timestamp: Long,
    val reason: String
)

fun DeviceWakeUpLog.toDeviceLogRecordOutputModel() = DeviceWakeUpLogOutputModel(
    deviceId = this.deviceId,
    timestamp = this.instant.epochSecond,
    reason = this.reason
)
data class DeviceWakeUpLogsOutputModel(val logs: List<DeviceWakeUpLogOutputModel>) {
    companion object {
        fun from(errors: List<DeviceWakeUpLog>) = DeviceWakeUpLogsOutputModel(errors.map { it.toDeviceLogRecordOutputModel() })
    }
}