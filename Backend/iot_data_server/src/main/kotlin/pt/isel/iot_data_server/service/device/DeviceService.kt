package pt.isel.iot_data_server.service.device

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.service.SensorDataService

@Service
class DeviceService (
    private val transactionManager: TransactionManager,
) {
    private val logger = LoggerFactory.getLogger(DeviceService::class.java)

    fun addDevice(device: Device): CreateDeviceResult {
        return transactionManager.run {
            val devices = it.repository.getAllDevices()
            if (devices.any { it.deviceId == device.deviceId }) {
                logger.info("Device with id ${device.deviceId} already exists")
                return@run Either.Left(CreateDeviceError.DeviceAlreadyExists)
            }
            it.repository.addDevice(device)

            logger.info("Device with id ${device.deviceId} added")
            return@run Either.Right(Unit)
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
}