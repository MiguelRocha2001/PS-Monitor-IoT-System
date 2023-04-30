package pt.isel.iot_data_server.repo.time_series

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.configuration.TSDBBuilder
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.domain.TemperatureRecord
import pt.isel.iot_data_server.repository.tsdb.TSDBRepository
import pt.isel.iot_data_server.utils.generateRandomPh
import pt.isel.iot_data_server.utils.getRandomInstantWithinLastWeek
import java.time.Instant
import kotlin.concurrent.thread

class SensorDataRepoConcurrentTests {
    private val tsdbBuilder: TSDBBuilder = TSDBBuilder("test_bucket")
    private val repo: TSDBRepository = TSDBRepository(
        tsdbBuilder.getClient(),
        tsdbBuilder.getBucket()
    )

    fun deleteAll() {
        deleteAllPhMeasurements(tsdbBuilder)
        deleteAllTemperatureMeasurements(tsdbBuilder)
    }

    fun `multiple threads add ph records and get them`() {
        val nOfThreads = 100
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"

        val phsBefore = repo.getPhRecords(deviceId)
        assertTrue("Ph found", phsBefore.isEmpty())

        val timestamps = mutableListOf<Instant>()

        val threads = mutableListOf<Thread>()
        for (i in 1..nOfThreads) {
            threads.add(thread {
                val instant = Instant.now()
                timestamps.add(instant)
                repo.savePhRecord(deviceId, PhRecord(generateRandomPh(), instant))
            })
        }
        threads.forEach { it.join() }

        val gotSameTimestamp = timestamps.distinct().size != nOfThreads

        val phs = repo.getPhRecords(deviceId)
        assertTrue(
            "Should be true",
            phs.size == nOfThreads && !gotSameTimestamp || phs.size < nOfThreads && gotSameTimestamp
        )
    }

    @Test
    fun `multiple threads add ph records and get them - multiple iterations`() {
        repeat(30) {
            deleteAll()
            `multiple threads add ph records and get them`()
        }
        deleteAll()
    }
}