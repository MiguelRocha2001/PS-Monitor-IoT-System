package pt.isel.iot_data_server.domain

import pt.isel.iot_data_server.utils.trimJsonString
import java.sql.Timestamp
import java.time.Instant

enum class SensorName {
    PH, TEMPERATURE, HUMIDITY, WATER_FLOW, WATER_LEVEL
}

data class PhRecord(val value: Double, val instant: Instant)
data class TemperatureRecord(val value: Int, val instant: Instant)
data class HumidityRecord(val value: Double, val instant: Instant)
data class WaterFlowRecord(val value: Int, val instant: Instant)
data class WaterLevelRecord(val value: Int, val instant: Instant)
data class FloodRecord(val instant: Instant)
data class SensorErrorRecord(val sensorName: SensorName, val instant: Instant)

fun fromJsonStringToPhRecord(str: String): PhRecord {
    val (time, value) = fromJsonStringToTimestampAndDouble(str)
    return PhRecord(value, time)
}

fun fromJsonStringToTemperatureRecord(str: String): TemperatureRecord {
    val (time, value) = fromJsonStringToTimestampAndInt(str)
    return TemperatureRecord(value, time)
}

fun fromJsonStringToHumidityRecord(str: String): HumidityRecord {
    val (time, value) = fromJsonStringToTimestampAndDouble(str)
    return HumidityRecord(value, time)
}

fun fromJsonStringToWaterFlowRecord(str: String): WaterFlowRecord {
    val (time, value) = fromJsonStringToTimestampAndInt(str)
    return WaterFlowRecord(value, time)
}

fun fromJsonStringToWaterLevelRecord(str: String): WaterLevelRecord {
    val (time, value) = fromJsonStringToTimestampAndInt(str)
    return WaterLevelRecord(value, time)
}

fun fromJsonStringToFloodRecord(str: String): FloodRecord {
    val time = fromJsonStringToTimestamp(str)
    return FloodRecord(time)
}

fun fromJsonStringToSensorErrorRecord(str: String): SensorErrorRecord {
    val time = fromJsonStringToTimestamp(str)
    val sensorName = fromJsonStringToSensorName(str)
    return SensorErrorRecord(sensorName, time)
}

fun fromJsonStringToTimestampAndDouble(str: String): Pair<Instant, Double> {
    val time = fromJsonStringToTimestamp(str)
    val value = fromJsonStringToDouble(str)
    return Pair(time, value)
}

fun fromJsonStringToTimestampAndInt(str: String): Pair<Instant, Int> {
    val time = fromJsonStringToTimestamp(str)
    val value = fromJsonStringToDouble(str).toInt()
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

fun fromJsonStringToValue(str: String): String {
    val split = str.trimJsonString().split(",")

    return split
        .find { it.contains("value") }
        ?.split(":")
        ?.get(1)
        ?.trim()
        ?.replace("\"", "")
        ?: throw IllegalArgumentException("Invalid json string")
}

fun fromJsonStringToSensorName(str: String): SensorName {
    val split = str.trimJsonString().split(",")

    val sensorName = split
        .find { it.contains("sensor_name") }
        ?.split(":")
        ?.get(1)
        ?.trim()
        ?.replace("\"", "")
        ?: throw IllegalArgumentException("Invalid json string")

    return SensorName.valueOf(sensorName)
}

fun fromJsonStringToDouble(str: String): Double {
    val value = fromJsonStringToValue(str)
    return value.toDouble()
}

fun fromJsonStringToInt(str: String): Int {
    val value = fromJsonStringToValue(str)
    return value.toInt()
}