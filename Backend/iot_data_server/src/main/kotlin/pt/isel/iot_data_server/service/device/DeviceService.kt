package pt.isel.iot_data_server.service.device

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.SEED
import pt.isel.iot_data_server.domain.generateRandomDeviceId
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.service.Either


@Service
class DeviceService (
    private val transactionManager: TransactionManager,
    private val seed: SEED = SEED.HOUR
) {
    private val logger = LoggerFactory.getLogger(DeviceService::class.java)

    fun addDevice(ownerEmail: String): CreateDeviceResult {
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
            val device = it.repository.getAllDevices().find { it.deviceId == deviceId }
            if (device == null) {
                logger.info("Device with id $deviceId not found")
                return@run Either.Left(GetDeviceError.DeviceNotFound)
            }
            logger.info("Device with id $deviceId found")
            return@run Either.Right(device)
        }
    }

    /**
     * Generates a random device ID
     * @param seed the seed to be used in the random number generator
     *  (exists to facilitate testing)
     */
    fun generateDeviceId(): DeviceId {
        // loops until a unique ID is generated
        while (true) {
            val deviceId = generateRandomDeviceId(seed)

            // Check if the generated ID already exists
            val exists = getDeviceById(deviceId) is Either.Right

            if (!exists)
                return deviceId
        }
    }
}