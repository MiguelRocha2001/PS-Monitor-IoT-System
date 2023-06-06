package pt.isel.iot_data_server.hive

import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.sensor_data.SensorErrorService

/**
 * This class is responsible for creating the admin (email=admin_email@gmail.com) user
 * and device (id=device_manual_tests) if they don't exist.
 */
@Component
class SensorReadingErrorMqttTopicHandler(
    private val deviceService: DeviceService,
    private val deviceSensorErrorService: SensorErrorService,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(SensorReadingErrorMqttTopicHandler::class.java)

    init {
        subscribeSensorErrorTopic(client)
        logger.info("Subscribed to error_reading_sensor topic")
    }
    private fun subscribeSensorErrorTopic(client: MqttClient) {
        client.subscribe("error_reading_sensor") { topic, message ->
            try {
                logger.info("Received message from topic: $topic")

                val byteArray = message.payload
                val string = String(byteArray)

                val sensorErrorRecord = fromMqttMsgStringToSensorErrorRecord(string)
                val deviceId = fromMqttMsgStringToDeviceId(string)

                val deviceResult = deviceService.getDeviceByIdOrNull(deviceId)
                if (deviceResult != null) {
                    // TODO: alert user immediately
                    deviceSensorErrorService.saveSensorErrorRecord(deviceId, sensorErrorRecord)
                    logger.info("Saved sensor error record: $sensorErrorRecord, from device: $deviceId")
                } else {
                    logger.info("Received sensor error record from unknown device: $deviceId")
                }
            } catch (e: Exception) {
                logger.error("Error while processing sensor error record: ${e.message}")
            }
        }
    }
}