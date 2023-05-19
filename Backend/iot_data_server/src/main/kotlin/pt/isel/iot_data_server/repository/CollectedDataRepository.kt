package pt.isel.iot_data_server.repository

import pt.isel.iot_data_server.domain.SensorRecord

interface CollectedDataRepository {
    fun getSensorRecords(deviceId: String, sensorName: String): List<SensorRecord>
    fun saveSensorRecord(deviceId: String, sensorRecord: SensorRecord)
    fun getSensorNames(): List<String>
}