package pt.isel.iot_data_server.repository

import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.domain.TemperatureRecord

interface CollectedDataRepository {


    // fun addDevice(device: Device)
   // fun getAllDevices(): List<Device>
    fun getPhRecords(deviceId: DeviceId): List<PhRecord>
    fun savePhRecord(deviceId: DeviceId, phRecord: PhRecord)
    fun getTemperatureRecords(deviceId: DeviceId): List<TemperatureRecord>
    fun saveTemperatureRecord(deviceId: DeviceId, temperatureRecord: TemperatureRecord)
    fun getAllPhRecords(): List<PhRecord>
    fun getAllTemperatureRecords(): List<TemperatureRecord>
}