package pt.isel.iot_data_server.repository

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceErrorRecord
import pt.isel.iot_data_server.domain.SensorErrorRecord

interface SensorDataRepository {
    abstract fun saveSensorAlertValue(type: String, value: Double)
    abstract fun getSensorAlertValue(type: String): Double?
}