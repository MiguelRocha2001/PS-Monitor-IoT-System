package pt.isel.iot_data_server

import org.eclipse.paho.client.mqttv3.MqttClient
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.*
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

    fun createUser(username: String, password: String) {
        transactionManager.run {
            it.repository.createUser(username, password)
        }
    }

    fun getUserByToken(token: String): User? {
        return transactionManager.run {
            return@run it.repository.getUserByToken(token)
        }
    }

    fun createToken(username: String, password: String) {
        transactionManager.run {
            it.repository.createToken(username, password)
        }
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
            it.repository.savePhRecord(deviceId, phRecord)
        }
    }

    fun getPhRecords(deviceId: DeviceId): List<PhRecord> {
        return transactionManager.run {
            return@run it.repository.getPhRecords(deviceId)
        }
    }

    fun saveTemperatureRecord(
        deviceId: DeviceId,
        temperatureRecord: PhRecord
    ) {
        transactionManager.run {

        }
    }

    fun getTemperatureRecords(deviceId: DeviceId): List<TemperatureRecord> {
        return transactionManager.run {
            return@run it.repository.getTemperatureRecords(deviceId)
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