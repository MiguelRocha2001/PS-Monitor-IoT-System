package pt.isel.iot_data_server.service.sensor_data

import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.tsdb.SensorDataRepo
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.email.EmailManager


@Service
class SensorDataService(
    private val emailSenderService: EmailManager,
    private val sensorDataRepo: SensorDataRepo,
    private val deviceService: DeviceService,
    private val sensorInfo: SensorInfo,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(SensorDataService::class.java)

    init {
        subscribeSensorTopic(client)
    }

    fun getSensorRecords(deviceId: String, sensorName: String): SensorDataResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(SensorDataError.DeviceNotFound)
        else
            Either.Right(sensorDataRepo.getSensorRecords(deviceId, sensorName))
    }

    fun getSensorRecordsIfIsOwner(deviceId: String, userId: String, sensorName: String): SensorDataResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(SensorDataError.DeviceNotFound)
        else if (!deviceService.belongsToUser(deviceId, userId))
            Either.Left(SensorDataError.DeviceNotBelongsToUser(userId))
        else
            getAvailableSensors(deviceId).firstOrNull { it == sensorName }?.let {
                Either.Right(sensorDataRepo.getSensorRecords(deviceId, sensorName))
            } ?: Either.Left(SensorDataError.SensorNotFound(sensorName))
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

    fun getAvailableSensors(deviceId: String): List<String> {
        return sensorDataRepo.getAvailableSensorTypes(deviceId)
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