package pt.isel.iot_data_server.service

import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.repository.TransactionManager

@Service
class DeviceService(
    private val transactionManager: TransactionManager,
) {
    fun createDevice(device: Device) {
        transactionManager.run {
            val devices = it.repository.getAllDevices()
            if (devices.any { it.deviceId == device.deviceId }) {
                throw Exception("Device already exists")
            }
            it.repository.addDevice(device)
        }
    }

    fun getAllDevices(): List<Device> {
        return transactionManager.run {
            return@run it.repository.getAllDevices()
        }
    }
}