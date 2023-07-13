package pt.isel.iot_data_server.configuration

import org.springframework.stereotype.Component
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.user.Role
import pt.isel.iot_data_server.service.user.UserService
import java.io.File
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
    private val FILE_NAME = "users-initialization.txt"
    private val file = File(FILE_NAME)
    init {
        logger.info("Initializing users")
        if (!file.exists()) {
            logger.info("Users initialization file not found. Skipping users initialization...")
        } else {
            logger.info("Sensor thresholds file found. Loading users...")
            file.forEachLine {
                val split1 = it.split(":").map { split -> split.trim() }
                val role = if (split1[0] == "admin") Role.ADMIN else Role.CLIENT
                val email = split1[1]
                val password = split1[2]
                val deviceId = if (split1[3] == "null") null else split1[3] // TODO: remove later (null is for testing
                val alertEmail = if (split1[4] == "null") null else split1[4] // TODO: remove later (null is for testing
                createUserAndDevice(userService, email, password, role, deviceId, alertEmail)
            }
            logger.info("Sensor thresholds loaded.")
        }
        /*
        createUserAndDevice( // TODO: remove later
            userService,
            "user_4_email@gmail.com",
            "user-4-password",
            Role.CLIENT,
            "IPGBJMUV",
            "a47128@alunos.isel.pt"
        )
         */
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
            logger.info("User ${username ?: userId} device created")
        } else {
            logger.info("User ${username ?: userId} device already exists. Skipping creation")
        }
    }
}