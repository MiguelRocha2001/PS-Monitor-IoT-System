package pt.isel.iot_data_server.http.model.device

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceLogRecord

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

data class DeviceErrorRecordOutputModel(
    val deviceId: String,
    val timestamp: String,
    val error: String
)

fun DeviceLogRecord.toDeviceErrorRecordOutputModel() = DeviceErrorRecordOutputModel(
    deviceId = this.deviceId,
    timestamp = this.instant.toString(),
    error = this.error
)
data class DeviceErrorsOutputModel(val errors: List<DeviceErrorRecordOutputModel>) {
    companion object {
        fun from(errors: List<DeviceLogRecord>) = DeviceErrorsOutputModel(errors.map { it.toDeviceErrorRecordOutputModel() })
    }
}