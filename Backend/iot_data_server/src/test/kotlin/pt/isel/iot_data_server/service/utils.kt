package pt.isel.iot_data_server.service

import org.junit.jupiter.api.Assertions
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.email.EmailManager
import pt.isel.iot_data_server.service.user.Role
import pt.isel.iot_data_server.service.user.SaltPasswordOperations
import pt.isel.iot_data_server.service.user.UserService
import pt.isel.iot_data_server.utils.generateRandomEmail

fun getNewDeviceAndUserService(transactionManager: TransactionManager): Pair<DeviceService, UserService> {
    val saltPasswordOperations = SaltPasswordOperations(transactionManager)
    val userService = UserService(transactionManager, saltPasswordOperations, EmailManager())
    val deviceService = DeviceService(transactionManager, userService)
    return deviceService to userService
}


/**
 * Creates a random user, with USER role and no password.
 * @return the user ID
 */

fun createRandomUser(userService: UserService): String {
    val CLIENT = userService.createUser(
        generateRandomEmail(),
        null,
        Role.CLIENT
    )
    Assertions.assertTrue(CLIENT is Either.Right)
    CLIENT as Either.Right
    return CLIENT.value.first // user ID
}

