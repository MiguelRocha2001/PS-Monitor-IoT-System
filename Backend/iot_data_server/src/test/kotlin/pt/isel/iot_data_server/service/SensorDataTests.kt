package pt.isel.iot_data_server.service

import org.junit.jupiter.api.Test
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.domain.TemperatureRecord
import pt.isel.iot_data_server.repository.tsdb.TSDBRepository
import pt.isel.iot_data_server.service.sensor_data.SensorDataService
import java.time.Instant
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class SensorDataTests {
    /*
    @Test
    fun addPhDataTest() {
        val repo = TSDBRepository()
        val sensorData = SensorDataService(repo)
        val id = UUID.randomUUID()
        val deviceId = DeviceId(id)

        //get ph records, should be empty
        val emptyPhRecords = sensorData.getPhRecords(deviceId)
        assert(emptyPhRecords.size == 0)

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

        //get temperature records, should be empty
        val emptyTemperatureRecords = sensorData.getTemperatureRecords(deviceId)
        assert(emptyTemperatureRecords.size == 0)

        val time = Instant.now()
        val temperatureRecord = TemperatureRecord(7.5,time)
        sensorData.saveTemperatureRecord(deviceId, temperatureRecord)

        val temperatureRecords = sensorData.getTemperatureRecords(deviceId)
        assert(temperatureRecords.size == 1)
        assert(temperatureRecords[0].value == 7.5)
        assert(temperatureRecords[0].instant.equals(time))
    }


    @Test
    fun concurrentAddPhDataTest() {
        val repo = TSDBRepository()
        val sensorData = SensorDataService(repo)

        val numThreads = 10
        val numRecordsPerThread = 100

        // Spawn multiple threads to insert ph records concurrently
        val threads = mutableListOf<Thread>()
        repeat(numThreads) { threadId ->
            threads.add(Thread {
                val deviceId = DeviceId(UUID.randomUUID())
                repeat(numRecordsPerThread) { i ->
                    val time = Instant.now()
                    val phRecord = PhRecord(randomPh(), time)
                    sensorData.savePhRecord(deviceId, phRecord)
                }
            })
        }

        // Start all threads
        threads.forEach { it.start() }

        // Wait for all threads to complete
        threads.forEach { it.join() }

        // Verify that all records were inserted
        val allPhRecords = sensorData.getAllPhRecords()
        assert(allPhRecords.size == numThreads * numRecordsPerThread)
    }

    private fun randomPh(): Double {
        return ThreadLocalRandom.current().nextDouble(0.0, 14.0)
    }
     */
}
