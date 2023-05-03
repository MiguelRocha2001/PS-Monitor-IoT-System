package pt.isel.iot_data_server.service.sensor_data

import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.crypto.AES
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.tsdb.TSDBRepository
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.email.EmailManager


@Service
class PhDataService(
  //  private val transactionManager: TransactionManager,
    private val emailSenderService: EmailManager,
    private val tsdbRepository: TSDBRepository,
    private val deviceService: DeviceService,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(PhDataService::class.java)

    val MIN_PH = 6.0 // TODO: change this to other place and make it configurable
    init {
        subscribePhTopic(client)
    }

    fun savePhRecord(
        deviceId: String,
        phRecord: PhRecord,
    ) {
       // transactionManager.run {
        if(phRecord.value < 0 || phRecord.value > 14)
            throw IllegalArgumentException("Invalid pH value")
        else
            tsdbRepository.savePhRecord(deviceId, phRecord)
    }

    fun getPhRecords(userId: String, deviceId: String): PhDataResult {
        return if (!deviceService.existsDevice(userId, deviceId))
            Either.Left(PhDataError.DeviceNotFound)
        else
            Either.Right(tsdbRepository.getPhRecords(deviceId))
    }

    fun getAllPhRecords(): List<PhRecord> {
        return tsdbRepository.getAllPhRecords()
    }

    private fun subscribePhTopic(client: MqttClient) {
        client.subscribe("ph") { topic, message ->
            try {
                logger.info("Received message from topic: $topic")

                // TODO -> DECRYPT MESSAGE FIRST

                val byteArray = message.payload
                val string = String(byteArray)

                val phRecord = fromJsonStringToPhRecord(string)
                val deviceId = fromJsonStringToDeviceId(string)

                val deviceResult = deviceService.getDeviceByIdOrNull(deviceId)
                if (deviceResult != null) {
                    // sendEmailIfPhExceedsLimit(deviceId, phRecord, deviceResult.value) TODO: uncomment this later
                    savePhRecord(deviceId, phRecord)
                    logger.info("Saved ph record: $phRecord, from device: $deviceId")
                } else {
                    logger.info("Received ph record from unknown device: $deviceId")
                }
            } catch (e: Exception) {
                logger.error("Error while processing ph record: ${e.message}")
            }
        }
    }

    private fun sendEmailIfPhExceedsLimit(deviceId: String, phRecord: PhRecord,device: Device) {
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