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

fun fromMqttMsgStringToPhRecord(str: String): PhRecord {
    val (time, value) = fromMqttMsgStringToTimestampAndDouble(str)
    return PhRecord(value, time)
}

fun fromMqttMessageToTemperatureRecord(str: String): TemperatureRecord {
    val (time, value) = fromMqttMsgStringToTimestampAndInt(str)
    return TemperatureRecord(value, time)
}

fun fromMqttMsgStringToHumidityRecord(str: String): HumidityRecord {
    val (time, value) = fromMqttMsgStringToTimestampAndDouble(str)
    return HumidityRecord(value, time)
}

fun fromMqttMsgStringToWaterFlowRecord(str: String): WaterFlowRecord {
    val (time, value) = fromMqttMsgStringToTimestampAndInt(str)
    return WaterFlowRecord(value, time)
}

fun fromMqttMsgStringToWaterLevelRecord(str: String): WaterLevelRecord {
    val (time, value) = fromMqttMsgStringToTimestampAndInt(str)
    return WaterLevelRecord(value, time)
}

fun fromMqttMsgStringToFloodRecord(str: String): FloodRecord {
    val time = fromMqttMsgStringToTimestamp(str)
    return FloodRecord(time)
}

fun fromMqttMsgStringToSensorErrorRecord(str: String): SensorErrorRecord {
    val time = fromMqttMsgStringToTimestamp(str)
    val sensorName = fromMqttMsgStringToSensorName(str)
    return SensorErrorRecord(sensorName, time)
}

fun fromMqttMsgStringToTimestampAndDouble(str: String): Pair<Instant, Double> {
    val time = fromMqttMsgStringToTimestamp(str)
    val value = fromMqttMsgStringToDouble(str)
    return Pair(time, value)
}

fun fromMqttMsgStringToTimestampAndInt(str: String): Pair<Instant, Int> {
    val time = fromMqttMsgStringToTimestamp(str)
    val value = fromMqttMsgStringToDouble(str).toInt()
    return Pair(time, value)
}

fun fromMqttMsgStringToTimestamp(str: String): Instant {
    val split = str.split(",")

    val timestampInSeconds: Long = split
        .find { it.contains("timestamp") }
        ?.substringAfter(":")
        ?.trim()
        ?.toLong() ?: throw IllegalArgumentException("Invalid json string")

    // String to Timestamp
    val timestamp = Timestamp(timestampInSeconds * 1000).time
    return Instant.ofEpochMilli(timestamp)
}

fun fromMqttMsgStringToValue(str: String): String {
    val split = str.trimJsonString().split(",")

    return split
        .find { it.contains("value") }
        ?.split(":")
        ?.get(1)
        ?.trim()
        ?.replace("\"", "")
        ?: throw IllegalArgumentException("Invalid json string")
}

fun fromMqttMsgStringToSensorName(str: String): SensorName {
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

fun fromMqttMsgStringToDouble(str: String): Double {
    val value = fromMqttMsgStringToValue(str)
    return value.toDouble()
}