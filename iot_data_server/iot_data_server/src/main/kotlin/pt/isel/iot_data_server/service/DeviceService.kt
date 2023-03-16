package pt.isel.iot_data_server.service

import org.eclipse.paho.client.mqttv3.MqttClient
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.TransactionManager
import java.util.*

@Service
class DeviceService(
    private val transactionManager: TransactionManager,
) {
    fun createDevice(device: Device) {
        transactionManager.run {

        }
    }

    fun getAllDevices(): List<Device> {
        return transactionManager.run {
            return@run it.repository.getAllDevices()
        }
    }
}