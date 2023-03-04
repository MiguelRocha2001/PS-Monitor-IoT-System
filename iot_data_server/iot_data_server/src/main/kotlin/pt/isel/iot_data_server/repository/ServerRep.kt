package pt.isel.iot_data_server.repository

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord

interface ServerRepository {
    fun addDevice(device: Device)
    fun addPhRecord(deviceId: DeviceId, phRecord: PhRecord)
}