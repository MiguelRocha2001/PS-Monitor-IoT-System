package pt.isel.iot_data_server.repository

import pt.isel.iot_data_server.domain.*
import java.time.Instant

interface CollectedDataRepository {


    // fun addDevice(device: Device)
   // fun getAllDevices(): List<Device>
    fun getPhRecords(deviceId: DeviceId): List<PhRecord>
    fun savePhRecord(deviceId: DeviceId, phRecord: PhRecord)
    fun getTemperatureRecords(deviceId: DeviceId): List<TemperatureRecord>
    fun saveTemperatureRecord(deviceId: DeviceId, temperatureRecord: TemperatureRecord)
}