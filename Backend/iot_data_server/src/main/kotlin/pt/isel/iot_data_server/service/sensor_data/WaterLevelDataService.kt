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
  //  private val transactionManager: TransactionManager,
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
       // transactionManager.run {
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
        client.subscribe("waterLevel") { _, message ->
            val waterLevelRecord = WaterLevelRecord(message.toString().toDouble())
            val deviceId = "waterLevel"
            saveWaterLevelRecord(deviceId, waterLevelRecord)
            logger.info("Water level record saved: $waterLevelRecord")
            if (waterLevelRecord.value < MIN_WATER_LEVEL) {
                emailSenderService.sendEmail(
                    "Water level is low",
                    "Water level is low, please refill the water tank"
                )
            }
        }
    }
}