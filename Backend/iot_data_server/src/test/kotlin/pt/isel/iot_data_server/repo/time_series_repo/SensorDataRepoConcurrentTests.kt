package pt.isel.iot_data_server.repo.time_series_repo

import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.configuration.TSDBBuilder
import pt.isel.iot_data_server.domain.SensorRecord
import pt.isel.iot_data_server.repository.tsdb.SensorDataRepo
import pt.isel.iot_data_server.utils.generateRandomPh
import java.time.Instant
import kotlin.concurrent.thread

class SensorDataRepoConcurrentTests {
    private val tsdbBuilder: TSDBBuilder = TSDBBuilder("test")
    private val repo: SensorDataRepo = SensorDataRepo(
        tsdbBuilder.getClient(),
        tsdbBuilder.getBucket()
    )

    fun deleteAll() {
        deleteAllSensorMeasurements(tsdbBuilder, "temperature")
    }

    fun `multiple threads add temperature records and get them`() {
        val nOfThreads = 100
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"

        val temperatureBefore = repo.getSensorRecords(deviceId, "temperature")
        assertTrue("Ph found", temperatureBefore.isEmpty())

        val timestamps = mutableListOf<Instant>()

        val threads = mutableListOf<Thread>()
        for (i in 1..nOfThreads) {
            threads.add(thread {
                val instant = Instant.now()
                timestamps.add(instant)
                val sensorRecord = SensorRecord("temperature", generateRandomPh(), instant)
                repo.saveSensorRecord(deviceId, sensorRecord = sensorRecord)
            })
        }
        threads.forEach { it.join() }

        val gotSameTimestamp = timestamps.distinct().size != nOfThreads

        val temperatures = repo.getSensorRecords(deviceId, "temperature")
        assertTrue(
            "Should be true",
            temperatures.size == nOfThreads && !gotSameTimestamp || temperatures.size < nOfThreads && gotSameTimestamp
        )
    }

    @Test
    fun `multiple threads add ph records and get them - multiple iterations`() {
        repeat(30) {
            deleteAll()
            `multiple threads add temperature records and get them`()
        }
        deleteAll()
    }
}