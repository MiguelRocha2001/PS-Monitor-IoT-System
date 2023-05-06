package pt.isel.iot_data_server.service.sensor_data

import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.tsdb.TSDBRepository
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.email.EmailManager


@Service
class SensorErrorService(
  //  private val transactionManager: TransactionManager,
    private val emailSenderService: EmailManager,
    private val tsdbRepository: TSDBRepository,
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
       // transactionManager.run {
        if(sensorErrorRecord.value < 0 || sensorErrorRecord.value > 100)
            throw IllegalArgumentException("Invalid sensorError value")
        else
            tsdbRepository.saveSensorErrorRecord(deviceId, sensorErrorRecord)
    }

    fun getSensorErrorRecords(deviceId: String): SensorErrorDataResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(SensorErrorDataError.DeviceNotFound)
        else
            Either.Right(tsdbRepository.getSensorErrorRecords(deviceId))
    }

    fun getSensorErrorRecordsIfIsOwner(deviceId: String, userId: String): SensorErrorDataResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(SensorErrorDataError.DeviceNotFound)
        else if (!deviceService.belongsToUser(deviceId, userId))
            Either.Left(SensorErrorDataError.DeviceNotBelongsToUser(userId))
        else
            Either.Right(tsdbRepository.getSensorErrorRecords(deviceId))
    }

    fun getAllSensorErrorRecords(): List<SensorErrorRecord> {
        return tsdbRepository.getAllSensorErrorRecords()
    }

    private fun subscribeSensorErrorTopic(client: MqttClient) {
        client.subscribe("sensor_error") { _, message ->
            val sensorErrorRecord = SensorErrorRecord.fromByteArray(message.payload)
            logger.info("Received sensorError record: $sensorErrorRecord")
            saveSensorErrorRecord(sensorErrorRecord.deviceId, sensorErrorRecord)
            emailSenderService.sendEmail(sensorErrorRecord)
        }
    }
}