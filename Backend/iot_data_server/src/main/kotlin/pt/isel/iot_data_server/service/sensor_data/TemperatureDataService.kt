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
class TemperatureDataService(
  //  private val transactionManager: TransactionManager,
    private val emailSenderService: EmailManager,
    private val tsdbRepository: TSDBRepository,
    private val deviceService: DeviceService,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(TemperatureDataService::class.java)

    val MIN_PH = 6.0 // TODO: change this to other place and make it configurable
    init {
        subscribeTemperatureTopic(client)
    }

    fun saveTemperatureRecord(
        deviceId: String,
        temperatureRecord: TemperatureRecord,
    ) {
        if(temperatureRecord.value < -273.15 || temperatureRecord.value > 1000)
            throw Exception("Invalid temperature value")
        tsdbRepository.saveTemperatureRecord(deviceId, temperatureRecord)
    }

    fun getTemperatureRecords(deviceId: String): TemperatureDataResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(TemperatureDataError.DeviceNotFound)
        else
            Either.Right(tsdbRepository.getTemperatureRecords(deviceId))
    }

    fun getTemperatureRecordsIfIsOwner(deviceId: String, userId: String): TemperatureDataResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(TemperatureDataError.DeviceNotFound)
        else if (!deviceService.belongsToUser(deviceId, userId))
            Either.Left(TemperatureDataError.DeviceNotBelongsToUser(userId))
        else
            Either.Right(tsdbRepository.getTemperatureRecords(deviceId))
    }

    fun getAllTemperatureRecords(): List<TemperatureRecord> {
        return tsdbRepository.getAllTemperatureRecords()
    }

    private fun subscribeTemperatureTopic(client: MqttClient) {
        client.subscribe("temperature") { topic, message ->
            try {
                logger.info("Received message from topic: $topic")

                val byteArray = message.payload
                val string = String(byteArray)

                val temperatureRecord = fromMqttMessageToTemperatureRecord(string)
                val deviceId = fromMqttMsgStringToDeviceId(string)

                val deviceResult = deviceService.getDeviceByIdOrNull(deviceId)
                if (deviceResult != null) {
                    // sendEmailIfPhExceedsLimit(deviceId, phRecord, deviceResult.value) TODO: uncomment this later
                    saveTemperatureRecord(deviceId, temperatureRecord)
                    logger.info("Saved temperature record: $temperatureRecord, from device: $deviceId")
                } else {
                    logger.info("Received temperature record from unknown device: $deviceId")
                }
            } catch (e: Exception) {
                logger.error("Error while processing temperature record: ${e.message}")
            }
        }
    }

    private fun sendEmailIfTemperatureExceedsLimit(deviceId: String, phRecord: PhRecord,device: Device) {
        if (phRecord.value < MIN_PH) {
            val bodyMessage = mapOf(
                "device_id" to deviceId,
                "ph_level" to phRecord.value.toString(),
                "ph_limit" to MIN_PH.toString()
            )
            val subject = emptyMap<String, String>()
            emailSenderService.sendEmail(device.ownerEmail, subject, bodyMessage,"phProblem")
        }
    }

}