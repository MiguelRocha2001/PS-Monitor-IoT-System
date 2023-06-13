package pt.isel.iot_data_server.mqtt

import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pt.isel.iot_data_server.domain.fromMqttMsgStringToDeviceId
import pt.isel.iot_data_server.domain.fromMqttMsgStringToDeviceLogRecord
import pt.isel.iot_data_server.service.device.DeviceLogService
import pt.isel.iot_data_server.service.device.DeviceService

/**
 * This class is responsible for creating the admin (email=admin_email@gmail.com) user
 * and device (id=device_manual_tests) if they don't exist.
 */
@Component
class DeviceWakeUpLogMqttTopicHandler(
    private val deviceService: DeviceService,
    private val deviceLogService: DeviceLogService,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(DeviceWakeUpLogMqttTopicHandler::class.java)

    init {
        subscribeDeviceWakeUpLogTopic(client)
        logger.info("Subscribed to device_wake_up_log topic")
    }
    private fun subscribeDeviceWakeUpLogTopic(client: MqttClient) {
        client.subscribe("device_wake_up_log") { topic, message ->
            try {
                logger.info("Received message from topic: $topic")

                val byteArray = message.payload
                val string = String(byteArray)

                val deviceErrorRecord = fromMqttMsgStringToDeviceLogRecord(string)
                val deviceId = fromMqttMsgStringToDeviceId(string)

                val deviceResult = deviceService.getDeviceByIdOrNull(deviceId)
                if (deviceResult != null) {
                    // TODO: alert user immediately
                    deviceLogService.saveDeviceLogRecord(deviceId, deviceErrorRecord)
                    logger.info("Saved device log: $deviceErrorRecord")
                } else {
                    logger.info("Received device log record from unknown device: $deviceId")
                }
            } catch (e: Exception) {
                logger.error("Error while processing device log record", e)
            }
        }
    }
}