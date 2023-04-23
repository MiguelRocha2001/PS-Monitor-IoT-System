package pt.isel.iot_data_server.service


import org.springframework.stereotype.Service
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.user.UserService

@Service
class DataEraserService(
    private val transactionManager: TransactionManager,
    private val deviceService: DeviceService,
    private val userService: UserService
) {
    fun eraseAllData() {
        // todo: should delete, also, sensor data
        transactionManager.run {
            deviceService.deleteAllDevices()
            userService.deleteAllUsers()
        }
    }
}






