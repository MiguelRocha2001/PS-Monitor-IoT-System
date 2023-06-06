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
class AdminInfoCreation(
    userService: UserService,
    private val deviceService: DeviceService
) {
    private val logger = Logger.getLogger(AdminInfoCreation::class.java.name)
    init {
        val users = userService.getAllUsers(Role.ADMIN)
        if (users.isEmpty()) { // enforces that there is always an admin user
            val result1 = userService.createUser("admin_email@gmail.com", "admin-password", Role.ADMIN)
            check(result1 is Either.Right)
            logger.info("Admin user created")

            val adminId = result1.value.first
            createAdminDeviceIfNonexistent(adminId)
        } else {
            logger.info("Admin user already exists. Skipping creation")

            val admin = userService.getUserByEmail("admin_email@gmail.com")
            check(admin != null)
            val adminId = admin.id
            createAdminDeviceIfNonexistent(adminId)
        }
    }

    private fun createAdminDeviceIfNonexistent(adminId: String) {
        val result2 = deviceService.getUserDevices(adminId)
        check(result2 is Either.Right)
        val adminDevices = result2.value

        val deviceExists = adminDevices.find { it.deviceId == "device_manual_tests" } != null

        if (!deviceExists) {
            val result3 = deviceService.createDeviceWithId(adminId, "device_manual_tests", "admin_alert_email@gmail.com")
            check(result3 is Either.Right)
            logger.info("Admin device created")
        } else {
            logger.info("Admin device already exists. Skipping creation")
        }
    }
}