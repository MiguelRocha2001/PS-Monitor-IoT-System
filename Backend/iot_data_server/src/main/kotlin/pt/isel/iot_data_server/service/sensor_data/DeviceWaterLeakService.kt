package pt.isel.iot_data_server.service.sensor_data

import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.tsdb.TSDBRepository
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.email.EmailManager


@Service
class WaterLeakDataService(
  //  private val transactionManager: TransactionManager,
    private val emailSenderService: EmailManager,
    private val tsdbRepository: TSDBRepository,
    private val deviceService: DeviceService,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(WaterLeakDataService::class.java)

    init {
        subscribeWaterLeakTopic(client)
    }

    private fun subscribeWaterLeakTopic(client: MqttClient) {
        client.subscribe("water_leak") { topic, message ->
            try {
                logger.info("Received message from topic: $topic")

                val byteArray = message.payload
                val string = String(byteArray)

                val floodRecord = fromMqttMsgStringToFloodRecord(string)
                val deviceId = fromMqttMsgStringToDeviceId(string)

                // TODO -> alert user

            } catch (e: Exception) {
                logger.error("Error while processing ph record: ${e.message}")
            }
        }
    }

}