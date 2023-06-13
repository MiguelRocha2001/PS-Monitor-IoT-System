package pt.isel.iot_data_server.mqtt

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
        val upperThreshold = sensorInfo.getUpperSensorThreshold(sensorRecord.type)
        val lowerThreshold = sensorInfo.getSensorLowerThreshold(sensorRecord.type)
        if (
            upperThreshold != null && sensorRecord.value > upperThreshold ||
            lowerThreshold != null && sensorRecord.value < lowerThreshold
        ) {
            sendEmailAlert(sensorRecord, device, lowerThreshold, upperThreshold)
        }
    }

    private fun sendEmailAlert(sensorRecord: SensorRecord, device: Device, lowerLimit: Double?, upperLimit: Double? = null) {
        val bodyMessage = mapOf(
            "device_id" to device.deviceId,
            "sensor_type" to sensorRecord.type,
            "value_read" to sensorRecord.value.toString(),
            "lower_limit" to (lowerLimit.toString()),
            "upper_limit" to (upperLimit.toString())
        )
        val subject = emptyMap<String, String>()
        emailSenderService.sendEmail(device.ownerEmail, subject, bodyMessage,"phProblem")
    }
}