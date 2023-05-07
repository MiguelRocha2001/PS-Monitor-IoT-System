package pt.isel.iot_data_server.repository

import pt.isel.iot_data_server.domain.*

interface CollectedDataRepository {


    // fun addDevice(device: Device)
   // fun getAllDevices(): List<Device>
    fun getPhRecords(deviceId: String): List<PhRecord>
    fun savePhRecord(deviceId: String, phRecord: PhRecord)
    fun getTemperatureRecords(deviceId: String): List<TemperatureRecord>
    fun saveTemperatureRecord(deviceId: String, temperatureRecord: TemperatureRecord)
    fun getAllPhRecords(): List<PhRecord>
    fun getAllTemperatureRecords(): List<TemperatureRecord>
    fun getHumidityRecords(deviceId: String): List<HumidityRecord>
    fun saveHumidityRecord(deviceId: String, humidityRecord: HumidityRecord)
    fun getAllHumidityRecords(): List<HumidityRecord>
    fun getWaterFlowRecords(deviceId: String): List<WaterFlowRecord>
    fun saveWaterFlowRecord(deviceId: String, waterFlowRecord: WaterFlowRecord)
    fun getAllWaterFlowRecords(): List<WaterFlowRecord>
    fun getWaterLevelRecords(deviceId: String): List<WaterLevelRecord>
    fun saveWaterLevelRecord(deviceId: String, waterLevelRecord: WaterLevelRecord)
    fun getAllWaterLevelRecords(): List<WaterLevelRecord>
}