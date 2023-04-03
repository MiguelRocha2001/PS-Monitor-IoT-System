package pt.isel.iot_data_server.service

import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.tsdb.TSDBRepository
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.email.EmailSender
import pt.isel.iot_data_server.service.sensor_data.PhDataError
import pt.isel.iot_data_server.service.sensor_data.PhDataResult

// TODO -> SOLVE CONCURRENCY PROBLEMS

@Service
class SensorDataService(
  //  private val transactionManager: TransactionManager,
    private val emailSenderService: EmailSender,
    private val tsdbRepository: TSDBRepository,
    private val deviceService: DeviceService,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(SensorDataService::class.java)
    private final val MIN_PH = 6.0
    init {
        subscribePhTopic(client)
    }

    fun savePhRecord(
        deviceId: DeviceId,
        phRecord: PhRecord,
    ) {
       // transactionManager.run {
        if(phRecord.value < 0 || phRecord.value > 14)
            throw Exception("Not a valid ph value")
        else
            tsdbRepository.savePhRecord(deviceId, phRecord)
    }

    fun getPhRecords(deviceId: DeviceId): PhDataResult {
        return if (deviceService.getDeviceById(deviceId) == null)
            Either.Left(PhDataError.DeviceNotFound)
        else
            Either.Right(tsdbRepository.getPhRecords(deviceId))
        /*
        return transactionManager.run {
            return@run it.repository.getPhRecords(deviceId)
        }*/
    }

    fun saveTemperatureRecord(
        deviceId: DeviceId,
        temperatureRecord: TemperatureRecord,
    ) {
        /*
        transactionManager.run {
            it.repository.saveTemperatureRecord(deviceId, temperatureRecord)
        }*/
        if(temperatureRecord.value < -273.15 || temperatureRecord.value > 1000)
            throw Exception("Invalid temperature value")
        tsdbRepository.saveTemperatureRecord(deviceId, temperatureRecord)
    }

    fun getTemperatureRecords(deviceId: DeviceId): List<TemperatureRecord> {
       /* return transactionManager.run {
            return@run it.repository.getTemperatureRecords(deviceId)
        }*/
        val data = tsdbRepository.getTemperatureRecords(deviceId)
        return tsdbRepository.getTemperatureRecords(deviceId)
    }

    private fun subscribePhTopic(client: MqttClient) {
        client.subscribe("/ph") { topic, message ->
            try {
                logger.info("Received message from topic: $topic")

                val byteArray = message.payload
                val string = String(byteArray)

                val phRecord = fromJsonStringToPhRecord(string)
                val deviceId = fromJsonStringToDeviceId(string)
                val device = deviceService.getDeviceById(deviceId)
                if (device != null) {
                   // sendEmailIfPhExceedsLimit(deviceId, phRecord,device)
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

    fun getAllPhRecords(): List<PhRecord> {
        return tsdbRepository.getAllPhRecords()
    }

    fun getAllTemperatureRecords(): List<TemperatureRecord> {
        return tsdbRepository.getAllTemperatureRecords()
    }


}