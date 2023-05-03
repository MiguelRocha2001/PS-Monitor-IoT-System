package pt.isel.iot_data_server.service.sensor_data

import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.tsdb.TSDBRepository
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.email.EmailManager


@Service
class FloodDataService(
  //  private val transactionManager: TransactionManager,
    private val emailSenderService: EmailManager,
    private val tsdbRepository: TSDBRepository,
    private val deviceService: DeviceService,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(FloodDataService::class.java)

    val MIN_PH = 6.0 // TODO: change this to other place and make it configurable
    init {
        subscribeFloodTopic(client)
    }

    private fun subscribeFloodTopic(client: MqttClient) {
        client.subscribe("flood") { topic, message ->
            try {
                logger.info("Received message from topic: $topic")

                // TODO -> DECRYPT MESSAGE FIRST

                val byteArray = message.payload
                val string = String(byteArray)

                val floodRecord = fromJsonStringToFloodRecord(string)
                val deviceId = fromJsonStringToDeviceId(string)

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