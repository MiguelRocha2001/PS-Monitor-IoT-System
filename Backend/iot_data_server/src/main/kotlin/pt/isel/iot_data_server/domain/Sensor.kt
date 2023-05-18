package pt.isel.iot_data_server.domain

import pt.isel.iot_data_server.utils.removeJsonBrackets
import java.sql.Timestamp
import java.time.Instant

interface SensorInfo {
    fun getSensorThreshold(sensorName: String): Double?
}

data class SensorRecord(val type: String, val value: Double, val instant: Instant)
data class SensorErrorRecord(val sensorName: String, val instant: Instant)

// TODO: use built in json parser

fun fromMqttMsgStringToSensorRecord(str: String): SensorRecord {
    val split = str.removeJsonBrackets().split(",")

    val name = getName(split)
    val instant = getInstant(split)

    val value = split
        .find { it.contains("\"value\"") }
        ?.split(":")
        ?.get(1)
        ?.trim()
        ?.trim('"')
        ?.toDouble()
        ?: throw IllegalArgumentException("Invalid json string")

    return SensorRecord(name, value, instant)
}

fun getInstant(split: List<String>): Instant {
    val timestampInSeconds: Long = split
        .find { it.contains("\"timestamp\"") }
        ?.substringAfter(":")
        ?.trim()
        ?.trim('"')
        ?.toLong() ?: throw IllegalArgumentException("Invalid string")

    // String to Timestamp
    val timestamp = Timestamp(timestampInSeconds * 1000).time
    return Instant.ofEpochMilli(timestamp)
}

private fun getName(split: List<String>): String {
    return split
        .find { it.contains("\"sensor_type\"") }
        ?.split(":")
        ?.get(1)
        ?.trim()
        ?.trim('"')
        ?: throw IllegalArgumentException("Invalid json string")
}

fun fromMqttMsgStringToSensorErrorRecord(str: String): SensorErrorRecord {
    val split = str.split(",")
    val name = getName(split)
    val instant = getInstant(split)
    return SensorErrorRecord(name, instant)
}