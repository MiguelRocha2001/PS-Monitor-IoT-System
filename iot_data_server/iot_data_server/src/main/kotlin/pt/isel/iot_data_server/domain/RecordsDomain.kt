package pt.isel.iot_data_server.domain

import java.sql.Timestamp

data class PhRecord(val value: Double, val timestamp: Timestamp)

data class TemperatureRecord(val value: Double, val timestamp: Timestamp)