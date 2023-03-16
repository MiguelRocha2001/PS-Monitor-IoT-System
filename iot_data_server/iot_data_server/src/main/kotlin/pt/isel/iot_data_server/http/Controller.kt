package pt.isel.iot_data_server.http

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.Service
import pt.isel.iot_data_server.domain.User
import java.util.*

@RestController
class Controller(
    val service: Service
) {

    @PostMapping("/users")
    fun createUser(
        @RequestBody userModel: CreateUserInputModel
    ) {
        service.createUser(userModel.username, userModel.password)
    }


    @PostMapping("/token")
    fun createToken(user: User) {
        service.createToken(user.id)
    }

    @PostMapping("/device")
    fun addDevice(
        @RequestBody deviceModel: InputDeviceModel
    ) {
        service.createDevice(deviceModel.toDevice())
    }
    @PostMapping("/device/{device_id}/ph")
    fun addPhRecord(
        @PathVariable device_id: String,
        @RequestBody phRecordModel: InputPhRecordModel
    ) {
        val deviceId = DeviceId(UUID.fromString(device_id))
        service.savePhRecord(deviceId, phRecordModel.toPhRecord())
    }

    @GetMapping("/device/{device_id}/ph")
    fun getPhRecords(
        @PathVariable device_id: String
    ) {
        val deviceId = DeviceId(UUID.fromString(device_id))
        service.getPhRecords(deviceId)
    }

    @PostMapping("/device/{device_id}/temperature")
    fun addTemperatureRecord(
        @PathVariable device_id: String,
        @RequestBody temperatureRecordModel: InputTemperatureRecordModel
    ) {
        val deviceId = DeviceId(UUID.fromString(device_id))
        service.saveTemperatureRecord(deviceId, temperatureRecordModel.toTemperatureRecord())
    }

    @GetMapping("/device/{device_id}/temperature")
    fun getTemperatureRecords(
        @PathVariable device_id: String
    ) {
        val deviceId = DeviceId(UUID.fromString(device_id))
        service.getTemperatureRecords(deviceId)
    }

    /*
    @GetMapping("/mine")
    fun getMine() {
        val brokerUrl = "tcp://localhost:1883"
        val clientId = "test-client"

        val persistence = MemoryPersistence()
        val client = MqttClient(brokerUrl, clientId, persistence)

        val options = MqttConnectOptions()
        options.isCleanSession = true

        client.connect(options)

        val topic = "topic"
        val payload = "Hello, HiveMQ!".toByteArray()
        val message = MqttMessage(payload)
        client.publish(topic, message)

        client.disconnect()
    }
     */
}