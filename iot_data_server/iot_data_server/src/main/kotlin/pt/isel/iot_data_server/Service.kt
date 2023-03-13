package pt.isel.iot_data_server

import org.eclipse.paho.client.mqttv3.MqttClient
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.repository.TransactionManager

@Service
class Service(
    private val transactionManager: TransactionManager,
) {
    init {
        val client = MqttClient("tcp://localhost:1883", MqttClient.generateClientId())
        client.connect()
        subscribePhTopic(client)
    }

    fun createDevice(device: Device) {
        transactionManager.run {

        }
    }
    fun savePhRecord(
        deviceId: DeviceId,
        phRecord: PhRecord
    ) {
        transactionManager.run {

        }
    }

    private fun subscribePhTopic(client: MqttClient) {
        client.subscribe("topic/ph") { topic, message ->
            val byteArray = message.payload
            val string = String(byteArray)
            println("Received message on topic $topic: $string")
        }
    }
}