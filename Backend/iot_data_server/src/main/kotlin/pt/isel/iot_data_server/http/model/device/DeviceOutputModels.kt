package pt.isel.iot_data_server.http.model.device

import pt.isel.iot_data_server.domain.Device

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