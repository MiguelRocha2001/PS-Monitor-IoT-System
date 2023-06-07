package pt.isel.iot_data_server.domain

import pt.isel.iot_data_server.utils.removeJsonBrackets
import java.sql.Timestamp
import java.time.Instant

interface SensorInfo {
    fun getUpperSensorThreshold(sensorName: String): Double?
    fun getSensorLowerThreshold(sensorName: String): Double?
}

data class SensorRecord(val type: String, val value: Double, val instant: Instant)
data class SensorErrorRecord(val sensorType: String, val instant: Instant)

// TODO: use built in json parser

fun fromMqttMsgStringToSensorRecord(str: String): SensorRecord {
    val split = str.removeJsonBrackets().split(",")

    val name = getSensorType(split)
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
        ?.toLong() ?: throw IllegalArgumentException("Invalid json string (timestamp)")

    // String to Timestamp
    val timestamp = Timestamp(timestampInSeconds * 1000).time
    return Instant.ofEpochMilli(timestamp)
}

private fun getSensorType(split: List<String>): String {
    return split
        .find { it.contains("\"sensor_type\"") }
        ?.split(":")
        ?.get(1)
        ?.trim()
        ?.trim('"')
        ?: throw IllegalArgumentException("Invalid json string (name)")
}

fun fromMqttMsgStringToSensorErrorRecord(str: String): SensorErrorRecord {
    val split = str.split(",")
    val sensorType = getSensorType(split)
    val instant = getInstant(split)
    return SensorErrorRecord(sensorType, instant)
}