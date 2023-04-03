package pt.isel.iot_data_server.domain

import pt.isel.iot_data_server.utils.trimJsonString
import java.sql.Timestamp
import java.time.Instant

data class PhRecord(val value: Double, val timestamp: Instant)

fun fromJsonStringToPhRecord(str: String): PhRecord {
    val split = str.trimJsonString().split(",")

    val timestampInSeconds: Long = split
        .find { it.contains("timestamp") }
        ?.substringAfter(":")
        ?.replace("\"", "")
        ?.trim()
        ?.toLong() ?: throw IllegalArgumentException("Invalid json string")

    // String to Timestamp
    val time = Timestamp(timestampInSeconds * 1000).time

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