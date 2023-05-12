package pt.isel.iot_data_server.service.device

import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.service.email.EmailManager


@Service
class DeviceLogService(
    private val transactionManager: TransactionManager,
    private val emailSenderService: EmailManager,
    private val deviceService: DeviceService,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(DeviceLogService::class.java)

    init {
        subscribeDeviceLogTopic(client)
    }

    fun saveDeviceLogRecord(
        deviceId: String,
        deviceLogRecord: DeviceLogRecord,
    ) {
        transactionManager.run {
            it.deviceRepo.saveDeviceLogRecord(deviceId, deviceLogRecord)
        }
    }

    fun getDeviceLogRecords(deviceId: String): DeviceErrorRecordsResult {
        return transactionManager.run {
            if (!deviceService.existsDevice(deviceId))
                Either.Left(DeviceErrorRecordsError.DeviceNotFound)
            else
                Either.Right(it.deviceRepo.getDeviceLogRecords(deviceId))
        }
    }

    fun getDeviceLogRecordsIfIsOwner(deviceId: String, userId: String): DeviceErrorRecordsResult {
        return transactionManager.run {
            if (!deviceService.existsDevice(deviceId))
                Either.Left(DeviceErrorRecordsError.DeviceNotFound)
            else if (!deviceService.belongsToUser(deviceId, userId))
                Either.Left(DeviceErrorRecordsError.DeviceNotBelongsToUser(userId))
            else
                Either.Right(it.deviceRepo.getDeviceLogRecords(deviceId))
        }
    }
    private fun subscribeDeviceLogTopic(client: MqttClient) {
        client.subscribe("device_wake_up_reason") { topic, message ->
            try {
                logger.info("Received message from topic: $topic")

                val byteArray = message.payload
                val string = String(byteArray)

                val deviceErrorRecord = fromMqttMsgStringToDeviceLogRecord(string)
                val deviceId = fromMqttMsgStringToDeviceId(string)

                val deviceResult = deviceService.getDeviceByIdOrNull(deviceId)
                if (deviceResult != null) {
                    // TODO: alert user immediately
                    saveDeviceLogRecord(deviceId, deviceErrorRecord)
                    logger.info("Saved sensor error record from device: $deviceId")
                } else {
                    logger.info("Received sensor error record from unknown device: $deviceId")
                }
            } catch (e: Exception) {
                logger.error("Error while processing sensor error record: ${e.message}")
            }
        }
    }
}