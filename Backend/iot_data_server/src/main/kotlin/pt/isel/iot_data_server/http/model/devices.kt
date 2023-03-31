package pt.isel.iot_data_server.http.model

import pt.isel.iot_data_server.domain.Device

data class DevicesOutputModel(val devices: List<DeviceOutputModel>) {

    companion object {
        fun from(devices: List<Device>) = DevicesOutputModel(devices.map { it.toOutputModel() })
    }
}
fun Device.toOutputModel() = DeviceOutputModel(id = this.deviceId.id)

data class DeviceOutputModel(val id: String)