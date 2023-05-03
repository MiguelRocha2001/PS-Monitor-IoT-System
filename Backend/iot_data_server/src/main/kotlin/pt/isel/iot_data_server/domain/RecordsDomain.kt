package pt.isel.iot_data_server.domain

import pt.isel.iot_data_server.utils.trimJsonString
import java.sql.Timestamp
import java.time.Instant

data class PhRecord(val value: Double, val instant: Instant)
data class TemperatureRecord(val value: Double, val instant: Instant)
data class FloodRecord(val instant: Instant)

fun fromJsonStringToPhRecord(str: String): PhRecord {
    val (time, value) = fromJsonStringToTimestampAndValue(str)
    return PhRecord(value, time)
}

fun fromJsonStringToTemperatureRecord(str: String): TemperatureRecord {
    val (time, value) = fromJsonStringToTimestampAndValue(str)
    return TemperatureRecord(value, time)
}

fun fromJsonStringToFloodRecord(str: String): FloodRecord {
    val time = fromJsonStringToTimestamp(str)
    return FloodRecord(time)
}

fun fromJsonStringToTimestampAndValue(str: String): Pair<Instant, Double> {
    val time = fromJsonStringToTimestamp(str)
    val value = fromJsonStringToValue(str)
    return Pair(time, value)
}

fun fromJsonStringToTimestamp(str: String): Instant {
    val split = str.trimJsonString().split(",")

    val timestampInSeconds: Long = split
        .find { it.contains("timestamp") }
        ?.substringAfter(":")
        ?.replace("\"", "")
        ?.trim()
        ?.toLong() ?: throw IllegalArgumentException("Invalid json string")

    // String to Timestamp
    val timestamp = Timestamp(timestampInSeconds * 1000).time
    return Instant.ofEpochMilli(timestamp)
}

fun fromJsonStringToValue(str: String): Double {
    val split = str.trimJsonString().split(",")

    return split
        .find { it.contains("value") }
        ?.split(":")
        ?.get(1)
        ?.trim()
        ?.replace("\"", "")
        ?.toDouble() ?: throw IllegalArgumentException("Invalid json string")
}