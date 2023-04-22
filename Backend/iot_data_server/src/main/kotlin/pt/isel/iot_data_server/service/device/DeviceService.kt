package pt.isel.iot_data_server.service.device

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.SEED
import pt.isel.iot_data_server.domain.generateRandomDeviceId
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.service.user.UserService
import pt.isel.iot_data_server.utils.emailVerifier


@Service
class DeviceService (
    private val transactionManager: TransactionManager,
    private val userService: UserService,
    private val seed: SEED
) {
    private val logger = LoggerFactory.getLogger(DeviceService::class.java)

    fun addDevice(userId: String, alertEmail: String): CreateDeviceResult {
        if (!emailVerifier(alertEmail) ) {
            logger.debug("Invalid owner email")
            return Either.Left(CreateDeviceError.InvalidOwnerEmail)
        }
        return transactionManager.run {
            return@run generateDeviceId().let { deviceId ->
                val device = Device(deviceId, alertEmail)
                it.repository.addDevice(userId, device)
                logger.debug("Device with id ${device.deviceId} added")
                Either.Right(deviceId.id)
            }
        }
    }

    fun existsDevice(userId: String, deviceId: DeviceId): Boolean {
        return getUserDeviceById(userId, deviceId.id) != null
    }

    fun getAllDevices(userId: String): GetAllDevicesResult {
        userService.getUserByIdOrNull(userId) ?: return Either.Left(GetAllDevicesError.UserNotFound)
        return transactionManager.run {
            val devices = it.repository.getAllDevices(userId).also {
                logger.debug("All devices returned")
            }
            return@run Either.Right(devices)
        }
    }

    fun getUserDeviceById(userId: String, deviceId: DeviceId): GetDeviceResult {
        return transactionManager.run {
            val device = getUserDeviceById(userId, deviceId.id)
            if (device == null) {
                logger.debug("Device with id $deviceId not found")
                return@run Either.Left(GetDeviceError.DeviceNotFound)
            }
            logger.debug("Device with id $deviceId found")
            return@run Either.Right(device)
        }
    }

    private fun getUserDeviceById(userId: String, deviceId: String): Device? {
        userService.getUserByIdOrNull(userId) ?: return null
        return transactionManager.run {
            return@run it.repository.getAllDevices(userId)
                .find<Device> { device: Device -> device.deviceId.id == deviceId }
        }
    }

    fun getDeviceByIdOrNull(deviceId: String): Device? {
        return transactionManager.run {
            return@run it.repository.getDeviceById(deviceId)
        }
    }

    /**
     * Generates a random device ID
     * @param seed the seed to be used in the random number generator
     *  (exists to facilitate testing)
     */
    fun generateDeviceId(): DeviceId {

        // loops until a unique ID is generated
        while (true) { // TODO: change to a for loop with a max number of iterations
            val deviceId = generateRandomDeviceId(seed)

            // Check if the generated ID already exists
            val exists = getDeviceByIdOrNull(deviceId.id) != null

            if (!exists) return deviceId
        }
    }

    fun removeAllDevices() {
        return transactionManager.run {
            return@run it.repository.removeAllDevices()
        }
    }

    fun getDevicesByOwnerEmail(ownerEmail: String): List<Device> { //FIXME: WHAT IF THE EMAIL DOES NOT EXIST
        return transactionManager.run {
            return@run it.repository.getDevicesByOwnerEmail(ownerEmail)
        }
    }

    /*
    fun deleteDevice(deviceId: DeviceId): DeleteDeviceResult {
        return transactionManager.run {
            val device = getUserDeviceById(deviceId.id)
            if (device == null) {
                logger.info("Device with id $deviceId not found")
                return@run Either.Left(DeleteDeviceError.DeviceNotFound)
            }
            it.repository.deleteDevice(deviceId)
            logger.info("Device with id $deviceId deleted")
            return@run Either.Right(Unit)
        }
    }
     */
}