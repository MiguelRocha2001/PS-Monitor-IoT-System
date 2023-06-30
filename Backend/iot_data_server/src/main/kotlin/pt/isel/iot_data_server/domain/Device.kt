package pt.isel.iot_data_server.domain

import pt.isel.iot_data_server.utils.removeJsonBrackets
import pt.isel.iot_data_server.utils.trimJsonString
import java.time.Instant
import kotlin.random.Random


data class Device(val deviceId: String, val ownerEmail: String)
data class DeviceWakeUpLog(val deviceId: String, val instant: Instant, val reason: String)

fun fromMqttMsgStringToDeviceLogRecord(str: String): DeviceWakeUpLog {
    val split = str.removeJsonBrackets().split(",")

    val deviceId = split
        .find { it.contains("device_id") }
        ?.split(":")
        ?.get(1)
        ?.trim()
        ?.trim('"')
        ?: throw IllegalArgumentException("Invalid json string")

    val instant = getInstant(split)

    val error = split
        .find { it.contains("reason") }
        ?.split(":")
        ?.get(1)
        ?.trim()
        ?.trim('"')
        ?: throw IllegalArgumentException("Invalid json string")

    return DeviceWakeUpLog(deviceId, instant, error)
}

/**
 * Generates a random device ID
 * The size of the ID is 8 characters.
 * 2^32 = 4,294,967,296 possible combinations.
 * This means that we need 8 characters to represent all possible 2^32 combinations.
 */
fun generateRandomDeviceId(): String {
    // Use the current timestamp as the seed for your random number generator
    val timestamp = System.currentTimeMillis()
    val rand = Random(timestamp)

    // Generate a random string of characters for the device ID
    val sb = StringBuilder()
    val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    val length = 6
    for (i in 0 until length) {
        sb.append(characters[rand.nextInt(characters.length)])
    }

    return sb.toString()
}

fun fromMqttMsgStringToDeviceId(str: String): String {
    val split = str.trimJsonString().split(",")

    return split
        .find { it.contains("\"device_id\"") }
        ?.split(":")
        ?.get(1)
        ?.trim()
        ?.replace("\"", "")
        ?: throw IllegalArgumentException("Invalid json string")
}