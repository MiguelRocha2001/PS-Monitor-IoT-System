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
class WaterLevelDataService(
    private val emailSenderService: EmailManager,
    private val tsdbRepository: TSDBRepository,
    private val deviceService: DeviceService,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(WaterLevelDataService::class.java)

    val MIN_WATER_LEVEL = 0.0 // TODO: change this to other place and make it configurable

    init {
        subscribeWaterLevelTopic(client)
    }

    fun saveWaterLevelRecord(
        deviceId: String,
        waterLevelRecord: WaterLevelRecord,
    ) {
        if(waterLevelRecord.value < 0 || waterLevelRecord.value > 100)
            throw IllegalArgumentException("Invalid water level value")
        else
            tsdbRepository.saveWaterLevelRecord(deviceId, waterLevelRecord)
    }

    fun getWaterLevelRecords(deviceId: String): WaterLevelDataResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(WaterLevelDataError.DeviceNotFound)
        else
            Either.Right(tsdbRepository.getWaterLevelRecords(deviceId))
    }

    fun getWaterLevelRecordsIfIsOwner(deviceId: String, userId: String): WaterLevelDataResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(WaterLevelDataError.DeviceNotFound)
        else if (!deviceService.belongsToUser(deviceId, userId))
            Either.Left(WaterLevelDataError.DeviceNotBelongsToUser(userId))
        else
            Either.Right(tsdbRepository.getWaterLevelRecords(deviceId))
    }

    fun getAllWaterLevelRecords(): List<WaterLevelRecord> {
        return tsdbRepository.getAllWaterLevelRecords()
    }

    private fun subscribeWaterLevelTopic(client: MqttClient) {
        client.subscribe("water_level") { topic, message ->
            try {
                logger.info("Received message from topic: $topic")

                val byteArray = message.payload
                val string = String(byteArray)

                val waterLevelRecord = fromMqttMsgStringToWaterLevelRecord(string)
                val deviceId = fromMqttMsgStringToDeviceId(string)

                val deviceResult = deviceService.getDeviceByIdOrNull(deviceId)
                if (deviceResult != null) {
                    // TODO: alert user if water flow is has passed the maximum value
                    saveWaterLevelRecord(deviceId, waterLevelRecord)
                    logger.info("Saved water level record: $waterLevelRecord, from device: $deviceId")
                } else {
                    logger.info("Received water level record from unknown device: $deviceId")
                }
            } catch (e: Exception) {
                logger.error("Error while processing water level record: ${e.message}")
            }
        }
    }

}