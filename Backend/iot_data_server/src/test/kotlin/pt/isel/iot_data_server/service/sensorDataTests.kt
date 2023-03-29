package pt.isel.iot_data_server.service

import org.junit.jupiter.api.Test
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.domain.TemperatureRecord
import pt.isel.iot_data_server.repository.jdbi.TSDBRepository
import java.time.Instant
import java.util.*

class sensorDataTests {



    @Test
    fun addPhDataTest() {
        val repo = TSDBRepository()
        val sensorData = SensorDataService(repo)
        val id = UUID.randomUUID()
        val deviceId = DeviceId(id)
        val time = Instant.now()
        val phRecord = PhRecord(7.5,time)
        sensorData.savePhRecord(deviceId, phRecord)

        val phRecords = sensorData.getPhRecords(deviceId)
        assert(phRecords.size == 1)
        assert(phRecords[0].value == 7.5)
        assert(phRecords[0].timestamp.equals(time))
    }


    @Test
    fun addTemperatureDataTest() {
        val repo = TSDBRepository()
        val sensorData = SensorDataService(repo)
        val id = UUID.randomUUID()
        val deviceId = DeviceId(id)
        val time = Instant.now()
        val temperatureRecord = TemperatureRecord(7.5,time)
        sensorData.saveTemperatureRecord(deviceId, temperatureRecord)

        val temperatureRecords = sensorData.getTemperatureRecords(deviceId)
        assert(temperatureRecords.size == 1)
        assert(temperatureRecords[0].value == 7.5)
        assert(temperatureRecords[0].timestamp.equals(time))
    }



}
