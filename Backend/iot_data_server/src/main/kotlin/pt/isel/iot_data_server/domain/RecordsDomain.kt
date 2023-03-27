package pt.isel.iot_data_server.domain

import java.sql.Timestamp
import java.time.Instant

data class PhRecord(val value: Double, val timestamp: Instant)

data class TemperatureRecord(val value: Double, val timestamp: Instant)