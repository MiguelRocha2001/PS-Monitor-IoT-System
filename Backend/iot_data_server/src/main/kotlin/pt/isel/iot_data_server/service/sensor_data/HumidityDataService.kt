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
class HumidityDataService(
  //  private val transactionManager: TransactionManager,
    private val emailSenderService: EmailManager,
    private val tsdbRepository: TSDBRepository,
    private val deviceService: DeviceService,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(HumidityDataService::class.java)

    val MIN_HUMIDITY = 0.0 // TODO: change this to other place and make it configurable

    init {
        subscribeHumidityTopic(client)
    }

    fun saveHumidityRecord(
        deviceId: String,
        humidityRecord: HumidityRecord,
    ) {
       // transactionManager.run {
        if(humidityRecord.value < 0 || humidityRecord.value > 100)
            throw IllegalArgumentException("Invalid humidity value")
        else
            tsdbRepository.saveHumidityRecord(deviceId, humidityRecord)
    }

    fun getHumidityRecords(deviceId: String): HumidityDataResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(HumidityDataError.DeviceNotFound)
        else
            Either.Right(tsdbRepository.getHumidityRecords(deviceId))
    }

    fun getHumidityRecordsIfIsOwner(deviceId: String, userId: String): HumidityDataResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(HumidityDataError.DeviceNotFound)
        else if (!deviceService.belongsToUser(deviceId, userId))
            Either.Left(HumidityDataError.DeviceNotBelongsToUser(userId))
        else
            Either.Right(tsdbRepository.getHumidityRecords(deviceId))
    }

    fun getAllHumidityRecords(): List<HumidityRecord> {
        return tsdbRepository.getAllHumidityRecords()
    }

    private fun subscribeHumidityTopic(client: MqttClient) {
        client.subscribe("humidity") { topic, message ->
            try {
                logger.info("Received message from topic: $topic")

                val byteArray = message.payload
                val string = String(byteArray)

                val humidityRecord = fromMqttMsgStringToHumidityRecord(string)
                val deviceId = fromMqttMsgStringToDeviceId(string)

                val deviceResult = deviceService.getDeviceByIdOrNull(deviceId)
                if (deviceResult != null) {
                    // sendEmailIfPhExceedsLimit(deviceId, phRecord, deviceResult.value) TODO: uncomment this later
                    saveHumidityRecord(deviceId, humidityRecord)
                    logger.info("Saved humidity record: $humidityRecord, from device: $deviceId")
                } else {
                    logger.info("Received humidity record from unknown device: $deviceId")
                }
            } catch (e: Exception) {
                logger.error("Error while processing humidity record: ${e.message}")
            }
        }
    }
}