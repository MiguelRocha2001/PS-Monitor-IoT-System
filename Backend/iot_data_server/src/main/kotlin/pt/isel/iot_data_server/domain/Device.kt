package pt.isel.iot_data_server.domain

import org.springframework.stereotype.Component
import pt.isel.iot_data_server.utils.trimJsonString
import java.time.LocalDateTime
import kotlin.random.Random


data class DeviceId(val id: String)

enum class SEED { HOUR, MINUTE, SECOND, MILLISECOND, NANOSECOND }

/**
 * Generates a random device ID
 * The size of the ID is 8 characters.
 * 2^32 = 4,294,967,296 possible combinations.
 * This means that we need 8 characters to represent all possible 2^32 combinations.
 * 23^7 < 4,294,967,296 < 23^8
 */
fun generateRandomDeviceId(seedType: SEED): DeviceId {
    // Use the current hour as the seed for your random number generator
    val seed = when (seedType) {
        SEED.HOUR -> LocalDateTime.now().hour
        SEED.MINUTE -> LocalDateTime.now().minute
        SEED.SECOND -> LocalDateTime.now().second
        SEED.MILLISECOND -> LocalDateTime.now().second * 1000
        SEED.NANOSECOND -> LocalDateTime.now().nano
    }
    val rand = Random(seed)

    // Generate a random string of characters for the device ID
    val sb = StringBuilder()
    val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    val length = 8
    for (i in 0 until length) {
        sb.append(characters[rand.nextInt(characters.length)])
    }

    return DeviceId(sb.toString())
}

fun fromJsonStringToDeviceId(str: String): DeviceId {
    val split = str.trimJsonString().split(",")

    val id = split
        .find { it.contains("deviceId") }
        ?.split(":")
        ?.get(1)
        ?.trim()
        ?.replace("\"", "")
        ?: throw IllegalArgumentException("Invalid json string")

    return DeviceId(id)
}

data class Device(val deviceId: DeviceId, val ownerEmail: String, val ownerMobile: Long)