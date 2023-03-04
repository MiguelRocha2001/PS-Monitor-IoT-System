package pt.isel.iot_data_server.repository.local

import org.springframework.stereotype.Repository
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord

@Repository
class LocalRepository {
    private val devices = mutableListOf<Device>()
    private val phRecords = mutableMapOf<DeviceId, Set<PhRecord>>()

    fun addDevice(device: Device) {
        if (devices.any { it.deviceId == device.deviceId })
            throw IllegalArgumentException("Device with id ${device.deviceId} already exists")
        devices.add(device)
    }
    fun addPhRecord(deviceId: DeviceId, phRecord: PhRecord) {
        devices.find { it.deviceId == deviceId }
            ?: throw IllegalArgumentException("Device with id $deviceId does not exist")
        val records = phRecords[deviceId] ?: emptySet()
        phRecords[deviceId] = records + phRecord
    }
}