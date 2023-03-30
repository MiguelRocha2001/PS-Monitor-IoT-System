package pt.isel.iot_data_server.service

import org.eclipse.paho.client.mqttv3.MqttClient
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.domain.TemperatureRecord
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.repository.jdbi.TSDBRepository
import java.time.Instant

@Service
class SensorDataService(
  //  private val transactionManager: TransactionManager,
    private val tsdbRepository: TSDBRepository,
    client: MqttClient
) {

    //TODO SOLVE CONCURRENCY PROBLEMS
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
            val byteArray = message.payload
            val string = String(byteArray)
            println("Received message on topic $topic: $string")
        }
    }
}