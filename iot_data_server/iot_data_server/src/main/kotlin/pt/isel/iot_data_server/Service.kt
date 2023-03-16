package pt.isel.iot_data_server

import org.eclipse.paho.client.mqttv3.MqttClient
import org.postgresql.shaded.com.ongres.scram.common.util.CryptoUtil
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.TransactionManager
import java.security.CryptoPrimitive
import java.util.*

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

    fun createToken(userId: Int) {
        transactionManager.run {
            val token = UUID.randomUUID().toString()
            it.repository.addToken(userId, token)
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
        temperatureRecord: TemperatureRecord
    ) {
        transactionManager.run {
            it.repository.saveTemperatureRecord(deviceId, temperatureRecord)
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