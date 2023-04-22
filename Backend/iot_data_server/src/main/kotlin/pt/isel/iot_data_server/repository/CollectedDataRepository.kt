package pt.isel.iot_data_server.repository

import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.domain.TemperatureRecord

interface CollectedDataRepository {


    // fun addDevice(device: Device)
   // fun getAllDevices(): List<Device>
    fun getPhRecords(deviceId: String): List<PhRecord>
    fun savePhRecord(deviceId: String, phRecord: PhRecord)
    fun getTemperatureRecords(deviceId: String): List<TemperatureRecord>
    fun saveTemperatureRecord(deviceId: String, temperatureRecord: TemperatureRecord)
    fun getAllPhRecords(): List<PhRecord>
    fun getAllTemperatureRecords(): List<TemperatureRecord>

}