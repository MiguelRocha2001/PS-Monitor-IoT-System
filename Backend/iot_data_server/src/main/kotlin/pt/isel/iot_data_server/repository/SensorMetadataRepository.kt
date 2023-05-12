package pt.isel.iot_data_server.repository

interface SensorMetadataRepository {
    fun saveSensorAlertValue(type: String, value: Double)
    fun getSensorAlertValue(type: String): Double?
}