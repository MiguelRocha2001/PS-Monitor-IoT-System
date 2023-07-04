package pt.isel.iot_data_server.mqtt

import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pt.isel.iot_data_server.configuration.NeutralizationDeviceStabilizationTime
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.tsdb.SensorDataRepo
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.email.EmailManager
import java.time.Instant

/**
 * This class is responsible for creating the admin (email=admin_email@gmail.com) user
 * and device (id=device_manual_tests) if they don't exist.
 */
@Component
class DeviceWaterLeakMqttTopicHandler(
    private val emailSenderService: EmailManager,
    private val deviceService: DeviceService,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(DeviceWaterLeakMqttTopicHandler::class.java)

    init {
        subscribeSensorTopic(client)
        logger.info("Subscribed to water_leak topic")
    }
    private fun subscribeSensorTopic(client: MqttClient) {
        client.subscribe("water_leak") { topic, message ->
            try {
                logger.info("Received message from topic: $topic")

                val byteArray = message.payload
                val string = String(byteArray)

                val deviceId = fromMqttMsgStringToDeviceId(string)

                val deviceResult = deviceService.getDeviceByIdOrNull(deviceId)
                if (deviceResult != null) {
                    sendEmailAlert(deviceResult)
                    logger.info("Sent email alert to device: $deviceId")
                } else {
                    logger.info("Received water leak alert from unknown device: $deviceId")
                }
            } catch (e: Exception) {
                logger.error("Error while processing water leak alert: ${e.message}")
            }
        }
    }

    private fun sendEmailAlert(device: Device) {
        val bodyMessage = mapOf(
            "device_id" to device.deviceId,
            "sensor_type" to "water sensor",
        )
        val subject = emptyMap<String, String>()
        emailSenderService.sendEmail(device.ownerEmail, subject, bodyMessage,"phProblem") // TODO: change template
    }
}