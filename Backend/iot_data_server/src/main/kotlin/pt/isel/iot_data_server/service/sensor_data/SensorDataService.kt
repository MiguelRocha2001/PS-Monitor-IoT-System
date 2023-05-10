package pt.isel.iot_data_server.service.sensor_data

import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.repository.tsdb.TSDBRepository
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.email.EmailManager


@Service
class SensorDataService(
    private val emailSenderService: EmailManager,
    private val tsdbRepository: TSDBRepository,
    private val transactionManager: TransactionManager,
    private val deviceService: DeviceService,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(SensorDataService::class.java)

    init {
        subscribeSensorTopic(client)
    }

    fun saveSensorRecord(
        deviceId: String,
        sensorRecord: SensorRecord
    ) {
        tsdbRepository.saveSensorRecord(deviceId, sensorRecord)
    }

    fun getSensorRecords(deviceId: String, sensorName: String): SensorDataResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(SensorDataError.DeviceNotFound)
        else
            Either.Right(tsdbRepository.getSensorRecords(deviceId, sensorName))
    }

    fun getSensorRecordsIfIsOwner(deviceId: String, userId: String, sensorName: String): SensorDataResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(SensorDataError.DeviceNotFound)
        else if (!deviceService.belongsToUser(deviceId, userId))
            Either.Left(SensorDataError.DeviceNotBelongsToUser(userId))
        else
            Either.Right(tsdbRepository.getSensorRecords(deviceId, sensorName))
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
                    saveSensorRecord(deviceId, sensorRecord)
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
        transactionManager.run {
            val repo = it.sensorRepo
            val threshold = repo.getSensorAlertValue(sensorRecord.type)
            if (threshold != null && sensorRecord.value > threshold) {
                sendEmailAlert(sensorRecord, device, threshold)
            }
        }
    }

    fun getAvailableSensors(): List<String> {
        return tsdbRepository.getSensorNames()
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