package pt.isel.iot_data_server.repo.relational_repo

import org.junit.jupiter.api.Assertions
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.repository.DeviceDataRepository
import pt.isel.iot_data_server.repository.UserDataRepository
import pt.isel.iot_data_server.service.user.Role
import java.time.Instant
import java.util.*
import kotlin.random.Random

internal fun createUser(usersRepo: UserDataRepository, email: String): User {
    val userId = UUID.randomUUID().toString()
    val userInfo = UserInfo(email, Role.USER)
    val user = User(userId, userInfo)
    usersRepo.createUser(user)
    return user
}

/**
 * Uses local function to generate random device ID since this one doesnt depend on the timestamp.
 */
internal fun createDevice(
    deviceRepo: DeviceDataRepository,
    userId: String,
    alertEmail: String,
    instant: Instant = Instant.now()
): Device {
    val device = Device(generateRandomDeviceId(), alertEmail, instant)
    deviceRepo.createDevice(userId, device)
    return device
}

internal fun checkDeviceCountIsZero(deviceRepo: DeviceDataRepository) {
    val foundDevices = deviceRepo.getAllDevices()
    Assertions.assertEquals(0, foundDevices.size)
}

private fun generateRandomDeviceId(): String {
    // Use the current timestamp as the seed for your random number generator
    val timestamp = (1..1000000000).random()
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