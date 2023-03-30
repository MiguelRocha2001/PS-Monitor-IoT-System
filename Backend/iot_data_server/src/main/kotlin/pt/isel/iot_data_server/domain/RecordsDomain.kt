package pt.isel.iot_data_server.domain

import pt.isel.iot_data_server.utils.trimJsonString
import java.sql.Timestamp
import java.time.Instant

data class PhRecord(val value: Double, val timestamp: Instant)

fun fromJsonStringToPhRecord(str: String): PhRecord {
    val split = str.trimJsonString().split(",")

    val timestampString = split
        .find { it.contains("timestamp") }
        ?.substringAfter(":")
        ?.replace("\"", "")

    // String to Timestamp
    val time = Timestamp.valueOf(timestampString).time

    val value = split
        .find { it.contains("value") }
        ?.split(":")
        ?.get(1)
        ?.trim()
        ?.replace("\"", "")
        ?.toDouble() ?: 0.0

    return PhRecord(value, Instant.ofEpochMilli(time))
}

data class TemperatureRecord(val value: Double, val instant: Instant)