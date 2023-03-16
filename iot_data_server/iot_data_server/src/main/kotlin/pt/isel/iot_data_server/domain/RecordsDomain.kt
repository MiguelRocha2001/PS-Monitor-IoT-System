package pt.isel.iot_data_server.domain

import java.sql.Timestamp

data class PhRecord(val ph: Double, val timestamp: Timestamp)

data class TemperatureRecord(val temperature: Double, val timestamp: Timestamp)