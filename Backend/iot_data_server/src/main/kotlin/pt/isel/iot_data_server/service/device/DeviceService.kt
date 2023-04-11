package pt.isel.iot_data_server.service.device

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.SEED
import pt.isel.iot_data_server.domain.generateRandomDeviceId
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.utils.emailVerifier


@Service
class DeviceService (
    private val transactionManager: TransactionManager,
    private val seed: SEED
) {
    private val logger = LoggerFactory.getLogger(DeviceService::class.java)

    fun addDevice(ownerEmail: String): CreateDeviceResult {
        if (!emailVerifier(ownerEmail) ) {
            logger.info("Invalid owner email")
            return Either.Left(CreateDeviceError.InvalidOwnerEmail)
        }
        return transactionManager.run {
            return@run generateDeviceId().let { deviceId ->
                val device = Device(deviceId, ownerEmail)
                it.repository.addDevice(device)
                logger.info("Device with id ${device.deviceId} added")
                Either.Right(deviceId.id)
            }
        }
    }

    fun getAllDevices(): List<Device> {
        return transactionManager.run {
            return@run it.repository.getAllDevices()
        }
    }

    fun getDeviceById(deviceId: DeviceId): GetDeviceResult {
        return transactionManager.run {
            val device = getDeviceById(deviceId.id)
            if (device == null) {
                logger.info("Device with id $deviceId not found")
                return@run Either.Left(GetDeviceError.DeviceNotFound)
            }
            logger.info("Device with id $deviceId found")
            return@run Either.Right(device)
        }
    }

    private fun getDeviceById(deviceId: String): Device? {
        return transactionManager.run {
            return@run it.repository.getAllDevices().find<Device> { it.deviceId.id == deviceId }
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
            val exists = getDeviceById(deviceId.id) != null

            if (!exists) return deviceId
        }
    }

    fun removeAllDevices() {
        return transactionManager.run {
            return@run it.repository.removeAllDevices()
        }
    }

    fun getDevicesByOwnerEmail(ownerEmail: String): List<Device> {
        return transactionManager.run {
            return@run it.repository.getDevicesByOwnerEmail(ownerEmail)
        }
    }
}