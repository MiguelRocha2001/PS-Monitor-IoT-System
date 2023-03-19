package pt.isel.iot_data_server.http.controllers

import org.springframework.web.bind.annotation.RestController

@RestController
class OthersController(

) {
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