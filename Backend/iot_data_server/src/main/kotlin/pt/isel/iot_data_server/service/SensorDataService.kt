package pt.isel.iot_data_server.service

import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.jdbi.TSDBRepository

// TODO -> SOLVE CONCURRENCY PROBLEMS

@Service
class SensorDataService(
  //  private val transactionManager: TransactionManager,
    private val tsdbRepository: TSDBRepository,
    client: MqttClient
) {
    private val logger = LoggerFactory.getLogger(SensorDataService::class.java)

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

    fun getPhRecords(deviceId: DeviceId): List<PhRecord> {
        return tsdbRepository.getPhRecords(deviceId)
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
        return tsdbRepository.getTemperatureRecords(deviceId)
    }

    private fun subscribePhTopic(client: MqttClient) {
        client.subscribe("/ph") { topic, message ->
            logger.info("Received message from topic: $topic")

            val byteArray = message.payload
            val string = String(byteArray)

            println(string)

            val phRecord = fromJsonStringToPhRecord(string)
            val deviceId = fromJsonStringToDeviceId(string)

            savePhRecord(deviceId, phRecord)

            logger.info("Saved ph record: $phRecord, from device: $deviceId")
        }
    }
}