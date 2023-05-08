package pt.isel.iot_data_server.service.device

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.generateRandomDeviceId
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.service.user.UserService
import pt.isel.iot_data_server.utils.emailVerifier


@Service
class DeviceService (
    private val transactionManager: TransactionManager,
    private val userService: UserService,
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
                it.deviceRepo.createDevice(userId, device)
                logger.debug("Device with id ${device.deviceId} added")
                Either.Right(deviceId)
            }
        }
    }

    fun existsDevice(deviceId: String): Boolean {
        return transactionManager.run {
            it.deviceRepo.getDeviceById(deviceId) != null
        }
    }

    fun getDeviceCount(userId: String): DeviceCountResult {
        userService.getUserByIdOrNull(userId) ?: return Either.Left(DeviceCountError.UserNotFound)
        return transactionManager.run {
            val count = it.deviceRepo.deviceCount(userId)
            logger.debug("Device count returned")
            return@run Either.Right(count)
        }
    }

    fun getAllDevices(page: Int? = null, limit: Int? = null): GetAllDevicesResult {
        return transactionManager.run {
            val devices = it.deviceRepo.getAllDevices(page, limit).also {
                logger.debug("All devices returned")
            }
            return@run Either.Right(devices)
        }
    }

    fun getUserDevices(userId: String, page: Int? = null, limit: Int? = null): GetAllDevicesResult {
        userService.getUserByIdOrNull(userId) ?: return Either.Left(GetAllDevicesError.UserNotFound)
        return transactionManager.run {
            val devices = it.deviceRepo.getAllDevices(userId, page, limit).also {
                logger.debug("All devices returned")
            }
            return@run Either.Right(devices)
        }
    }

    fun getUserDeviceById(userId: String, deviceId: String): GetDeviceResult {
        return transactionManager.run {
            val device = getUserDeviceByIdOrNull(userId, deviceId)
            if (device == null) {
                logger.debug("Device with id $deviceId not found")
                return@run Either.Left(GetDeviceError.DeviceNotFound)
            }
            logger.debug("Device with id $deviceId found")
            return@run Either.Right(device)
        }
    }

    fun belongsToUser(deviceId: String, userId: String): Boolean {
        return getUserDeviceById(userId, deviceId) is Either.Right
    }

    private fun getUserDeviceByIdOrNull(userId: String, deviceId: String): Device? {
        userService.getUserByIdOrNull(userId) ?: return null
        return transactionManager.run {
            return@run it.deviceRepo.getAllDevices(userId)
                .find<Device> { device: Device -> device.deviceId == deviceId }
        }
    }

    fun getDeviceByIdOrNull(deviceId: String): Device? {
        return transactionManager.run {
            return@run it.deviceRepo.getDeviceById(deviceId)
        }
    }

    /**
     * Generates a random device ID
     * @param seed the seed to be used in the random number generator
     *  (exists to facilitate testing)
     */
    fun generateDeviceId(): String {
        // loops until a unique ID is generated
        while (true) { // TODO: change to a for loop with a max number of iterations
            val deviceId = generateRandomDeviceId()

            // Check if the generated ID already exists
            val exists = getDeviceByIdOrNull(deviceId) != null

            if (!exists) return deviceId
        }
    }

    fun removeAllDevices() {
        return transactionManager.run {
            return@run it.deviceRepo.removeAllDevices()
        }
    }

    fun getDevicesByOwnerEmail(ownerEmail: String): List<Device> { //FIXME: WHAT IF THE EMAIL DOES NOT EXIST
        return transactionManager.run {
            return@run it.deviceRepo.getDevicesByOwnerEmail(ownerEmail)
        }
    }

    fun deleteAllDevices() {
        return transactionManager.run {
            return@run it.deviceRepo.deleteAllDevices()
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