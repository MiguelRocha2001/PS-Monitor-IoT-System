package pt.isel.iot_data_server.service

import org.junit.jupiter.api.Assertions
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.user.SaltPasswordOperations
import pt.isel.iot_data_server.service.user.UserService
import pt.isel.iot_data_server.utils.generateRandomEmail
import pt.isel.iot_data_server.utils.generateRandomName
import pt.isel.iot_data_server.utils.generateRandomPassword

fun getNewDeviceAndUserService(transactionManager: TransactionManager): Pair<DeviceService, UserService> {
    val saltPasswordOperations = SaltPasswordOperations(transactionManager)
    val userService = UserService(transactionManager, saltPasswordOperations)
    val deviceService = DeviceService(transactionManager, userService)
    return deviceService to userService
}

fun createRandomUser(userService: UserService): String {
    val user = userService.createUser(
        UserInfo(
            generateRandomName(),
            generateRandomPassword(),
            generateRandomEmail()
        )
    )
    Assertions.assertTrue(user is Either.Right)
    user as Either.Right
    return user.value.first // user ID
}