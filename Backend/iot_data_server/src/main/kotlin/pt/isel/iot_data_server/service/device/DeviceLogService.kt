package pt.isel.iot_data_server.service.device

import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.DeviceWakeUpLog
import pt.isel.iot_data_server.domain.fromMqttMsgStringToDeviceId
import pt.isel.iot_data_server.domain.fromMqttMsgStringToDeviceLogRecord
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

    fun saveDeviceLogRecord(
        deviceId: String,
        deviceWakeUpLog: DeviceWakeUpLog,
    ) {
        transactionManager.run {
            it.deviceRepo.createDeviceLogRecord(deviceId, deviceWakeUpLog)
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
}