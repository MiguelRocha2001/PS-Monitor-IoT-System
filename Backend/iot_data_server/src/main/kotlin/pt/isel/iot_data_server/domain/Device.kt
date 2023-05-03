package pt.isel.iot_data_server.domain

import pt.isel.iot_data_server.utils.trimJsonString
import kotlin.random.Random


data class Device(val deviceId: String, val ownerEmail: String)

/**
 * Generates a random device ID
 * The size of the ID is 8 characters.
 * 2^32 = 4,294,967,296 possible combinations.
 * This means that we need 8 characters to represent all possible 2^32 combinations.
 * 23^7 < 4,294,967,296 < 23^8
 */
fun generateRandomDeviceId(): String {
    // Use the current hour as the seed for your random number generator
    val timestamp = System.currentTimeMillis()
    val rand = Random(timestamp)

    // Generate a random string of characters for the device ID
    val sb = StringBuilder()
    val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    val length = 8
    for (i in 0 until length) {
        sb.append(characters[rand.nextInt(characters.length)])
    }

    return sb.toString()
}

fun fromJsonStringToDeviceId(str: String): String {
    val split = str.trimJsonString().split(",")

    val deviceId = split
        .find { it.contains("device_id") }
        ?.split(":")
        ?.get(1)
        ?.trim()
        ?.replace("\"", "")
        ?: throw IllegalArgumentException("Invalid json string")

    return deviceId
}