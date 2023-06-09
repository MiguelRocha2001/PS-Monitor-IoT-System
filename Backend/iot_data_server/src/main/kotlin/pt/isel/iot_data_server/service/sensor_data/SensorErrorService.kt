package pt.isel.iot_data_server.service.sensor_data

import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.SensorErrorRecord
import pt.isel.iot_data_server.repository.TransactionManager
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
}