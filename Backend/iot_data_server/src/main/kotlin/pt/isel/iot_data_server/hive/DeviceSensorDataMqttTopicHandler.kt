package pt.isel.iot_data_server.hive

import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.tsdb.SensorDataRepo
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.email.EmailManager

/**
 * This class is responsible for creating the admin (email=admin_email@gmail.com) user
 * and device (id=device_manual_tests) if they don't exist.
 */
@Component
class DeviceSensorDataMqttTopicHandler(
    private val emailSenderService: EmailManager,
    private val deviceService: DeviceService,
    private val sensorInfo: SensorInfo,
    private val sensorDataRepo: SensorDataRepo,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(DeviceSensorDataMqttTopicHandler::class.java)

    init {
        subscribeSensorTopic(client)
        logger.info("Subscribed to sensor_record topic")
    }
    private fun subscribeSensorTopic(client: MqttClient) {
        client.subscribe("sensor_record") { topic, message ->
            try {
                logger.info("Received message from topic: $topic")

                val byteArray = message.payload
                val string = String(byteArray)

                val sensorRecord = fromMqttMsgStringToSensorRecord(string)
                val deviceId = fromMqttMsgStringToDeviceId(string)

                val deviceResult = deviceService.getDeviceByIdOrNull(deviceId)
                if (deviceResult != null) {
                    alertIfDangerous(deviceResult, sensorRecord)
                    sensorDataRepo.saveSensorRecord(deviceId, sensorRecord)
                    logger.info("Saved sensor record: $sensorRecord, from device: $deviceId")
                } else {
                    logger.info("Received sensor record from unknown device: $deviceId")
                }
            } catch (e: Exception) {
                logger.error("Error while processing sensor record: ${e.message}")
            }
        }
    }

    private fun alertIfDangerous(device: Device, sensorRecord: SensorRecord) {
        val threshold = sensorInfo.getSensorThreshold(sensorRecord.type)
        if (threshold != null && sensorRecord.value > threshold) {
            sendEmailAlert(sensorRecord, device, threshold)
        }
    }

    private fun sendEmailAlert(sensorRecord: SensorRecord, device: Device, limit: Double) {
        val bodyMessage = mapOf(
            "device_id" to device.deviceId,
            "ph_level" to sensorRecord.value.toString(),
            "ph_limit" to limit.toString()
        )
        val subject = emptyMap<String, String>()
        emailSenderService.sendEmail(device.ownerEmail, subject, bodyMessage,"phProblem")
    }
}