package pt.isel.iot_data_server.configuration

import org.springframework.stereotype.Component
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.user.Role
import pt.isel.iot_data_server.service.user.UserService
import java.util.logging.Logger

/**
 * This class is responsible for creating the admin (email=admin_email@gmail.com) user
 * and device (id=device_manual_tests) if they don't exist.
 */
@Component
class UsersInitialization(
    userService: UserService,
    private val deviceService: DeviceService
) {
    private val logger = Logger.getLogger(UsersInitialization::class.java.name)
    init {
        createUserAndDevice(
            userService,
            "admin_email@gmail.com",
                "admin-password",
            Role.ADMIN,
            null,
            null
        )
        createUserAndDevice(
            userService,
            "user_1_email@gmail.com",
            "user-1-password",
            Role.USER,
            "user-1-device-id",
            "user1-alert-email@gmail.com"
        )
        createUserAndDevice(
            userService,
            "user_2_email@gmail.com",
            "user-2-password",
            Role.USER,
            "user-2-device-id",
            "user2-alert-email@gmail.com"
        )
        createUserAndDevice(
            userService,
            "user_3_email@gmail.com",
            "user-3-password",
            Role.USER,
            "user-3-device-id",
            "user3-alert-email@gmail.com"
        )
    }

    private fun createUserAndDevice(
        userService: UserService,
        userEmail: String,
        password: String,
        role: Role,
        deviceId: String?,
        alertEmail: String?
    ) {
        val retrievedUser = userService.getUserByEmail(userEmail)
        if (retrievedUser == null) { // enforces that there is always an admin user
            val result1 = userService.createUser(userEmail, password, role)
            check(result1 is Either.Right)

            if (role == Role.ADMIN)
                logger.info("Admin user created")
            else
                logger.info("$userEmail user created")

            val userId = result1.value.first

            if (deviceId == null || alertEmail == null) return

            createUserDeviceIfNonexistent(
                userId,
                deviceId,
                alertEmail,
                username = userEmail
            )
        } else {
            logger.info("$userEmail user already exists. Skipping creation")
            val userId = retrievedUser.id

            if (deviceId == null || alertEmail == null) return

            createUserDeviceIfNonexistent(
                userId,
                deviceId,
                alertEmail,
                username = userEmail
            )
        }
    }

    private fun createUserDeviceIfNonexistent(userId: String, deviceId: String, alertEmail: String, username: String? = null) {
        val result2 = deviceService.getUserDevices(userId)
        check(result2 is Either.Right)
        val adminDevices = result2.value

        val deviceExists = adminDevices.find { it.deviceId == deviceId } != null

        if (!deviceExists) {
            val result3 = deviceService.createDeviceWithId(userId, deviceId, alertEmail)
            check(result3 is Either.Right)
            logger.info("${username ?: userId} device created")
        } else {
            logger.info("${username ?: userId} device already exists. Skipping creation")
        }
    }
}