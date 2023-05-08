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
class WaterFlowDataService(
  //  private val transactionManager: TransactionManager,
    private val emailSenderService: EmailManager,
    private val tsdbRepository: TSDBRepository,
    private val deviceService: DeviceService,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(WaterFlowDataService::class.java)

    val MAXIMUM_WATER_FLOW = 1000.0 // TODO: change this to other place and make it configurable

    init {
        subscribeWaterFlowTopic(client)
    }

    fun saveWaterFlowRecord(
        deviceId: String,
        waterFlowRecord: WaterFlowRecord
    ) {
        if (waterFlowRecord.value < 0)
            throw Exception("Invalid water flow value")
        tsdbRepository.saveWaterFlowRecord(deviceId, waterFlowRecord)
    }

    fun getWaterFlowRecords(deviceId: String): WaterFlowDataResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(WaterFlowDataError.DeviceNotFound)
        else
            Either.Right(tsdbRepository.getWaterFlowRecords(deviceId))
    }

    fun getWaterFlowRecordsIfIsOwner(deviceId: String, userId: String): WaterFlowDataResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(WaterFlowDataError.DeviceNotFound)
        else if (!deviceService.belongsToUser(deviceId, userId))
            Either.Left(WaterFlowDataError.DeviceNotBelongsToUser(userId))
        else
            Either.Right(tsdbRepository.getWaterFlowRecords(deviceId))
    }

    fun getAllWaterFlowRecords(): List<WaterFlowRecord> {
        return tsdbRepository.getAllWaterFlowRecords()
    }

    private fun subscribeWaterFlowTopic(client: MqttClient) {
        client.subscribe("water_flow") { topic, message ->
            try {
                logger.info("Received message from topic: $topic")

                val byteArray = message.payload
                val string = String(byteArray)

                val waterFlowRecord = fromMqttMsgStringToWaterFlowRecord(string)
                val deviceId = fromMqttMsgStringToDeviceId(string)

                val deviceResult = deviceService.getDeviceByIdOrNull(deviceId)
                if (deviceResult != null) {
                    // TODO: alert user if water flow is has passed the maximum value
                    saveWaterFlowRecord(deviceId, waterFlowRecord)
                    logger.info("Saved water flow record: $waterFlowRecord, from device: $deviceId")
                } else {
                    logger.info("Received water flow record from unknown device: $deviceId")
                }
            } catch (e: Exception) {
                logger.error("Error while processing water flow record: ${e.message}")
            }
        }
    }

}