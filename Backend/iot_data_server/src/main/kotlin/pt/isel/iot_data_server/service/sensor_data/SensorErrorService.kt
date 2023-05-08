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
class SensorErrorService(
    private val transactionManager: TransactionManager,
    private val emailSenderService: EmailManager,
    private val deviceService: DeviceService,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(SensorErrorService::class.java)

    init {
        subscribeSensorErrorTopic(client)
    }

    fun saveSensorErrorRecord(
        deviceId: String,
        sensorErrorRecord: SensorErrorRecord,
    ) {
        transactionManager.run {
            it.deviceRepo.saveSensorErrorRecord(deviceId, sensorErrorRecord)
        }
    }

    fun getSensorErrorRecords(deviceId: String): SensorErrorDataResult {
        return transactionManager.run {
            if (!deviceService.existsDevice(deviceId))
                Either.Left(SensorErrorDataError.DeviceNotFound)
            else
                Either.Right(it.deviceRepo.getSensorErrorRecords(deviceId))
        }
    }

    fun getSensorErrorRecordsIfIsOwner(deviceId: String, userId: String): SensorErrorDataResult {
        return transactionManager.run {
            if (!deviceService.existsDevice(deviceId))
                Either.Left(SensorErrorDataError.DeviceNotFound)
            else if (!deviceService.belongsToUser(deviceId, userId))
                Either.Left(SensorErrorDataError.DeviceNotBelongsToUser(userId))
            else
                Either.Right(it.deviceRepo.getSensorErrorRecords(deviceId))
        }
    }

    fun getAllSensorErrorRecords(): List<SensorErrorRecord> {
        return transactionManager.run {
            it.deviceRepo.getAllSensorErrorRecords()
        }
    }

    private fun subscribeSensorErrorTopic(client: MqttClient) {
        client.subscribe("sensor_error") { topic, message ->
            try {
                logger.info("Received message from topic: $topic")

                val byteArray = message.payload
                val string = String(byteArray)

                val sensorErrorRecord = fromJsonStringToSensorErrorRecord(string)
                val deviceId = fromJsonStringToDeviceId(string)

                val deviceResult = deviceService.getDeviceByIdOrNull(deviceId)
                if (deviceResult != null) {
                    // TODO: alert user immediately
                    saveSensorErrorRecord(deviceId, sensorErrorRecord)
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