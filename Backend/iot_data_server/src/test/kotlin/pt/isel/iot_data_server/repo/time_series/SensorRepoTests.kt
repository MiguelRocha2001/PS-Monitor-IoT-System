package pt.isel.iot_data_server.repo.time_series

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.configuration.TSDBBuilder
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.domain.TemperatureRecord
import pt.isel.iot_data_server.repository.tsdb.SensorDataRepo
import pt.isel.iot_data_server.utils.generateRandomPh
import java.time.Instant

class TsdbRepoTests {
    private val tsdbBuilder: TSDBBuilder = TSDBBuilder("test")
    private val repo: SensorDataRepo = SensorDataRepo(
        tsdbBuilder.getClient(),
        tsdbBuilder.getBucket()
    )

    @AfterEach
    fun deleteAll() {
        deleteAllPhMeasurements(tsdbBuilder)
        deleteAllTemperatureMeasurements(tsdbBuilder)
    }

    @Test
    fun `add ph record and get it`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"

        val phsBefore = repo.getPhRecords(deviceId)
        assertTrue("Ph found", phsBefore.isEmpty())

        repo.savePhRecord(deviceId, PhRecord(generateRandomPh(), Instant.now()))

        val phs = repo.getPhRecords(deviceId)
        assertTrue("Ph found", phs.size == 1)
    }

    @Test
    fun `add 3 ph records and get the list`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val phsBefore = repo.getPhRecords(deviceId)

        assertTrue("Ph found", phsBefore.isEmpty())

        repo.savePhRecord(deviceId, PhRecord(generateRandomPh(), Instant.now()))
        repo.savePhRecord(deviceId, PhRecord(generateRandomPh(), Instant.now()))
        repo.savePhRecord(deviceId, PhRecord(generateRandomPh(), Instant.now()))

        val phs = repo.getPhRecords(deviceId)
        assertTrue("Ph found", phs.size == 3)
    }

    @Test
    fun `add 2 ph records to a device and 1 to another`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val deviceId2 = "80acf16c-d3bb-11ed-afa1-0242ac120003"

        repo.savePhRecord(deviceId, PhRecord(generateRandomPh(), Instant.now()))
        repo.savePhRecord(deviceId, PhRecord(generateRandomPh(), Instant.now()))
        repo.savePhRecord(deviceId2, PhRecord(generateRandomPh(), Instant.now()))

        val phs = repo.getPhRecords(deviceId)
        val phs2 = repo.getPhRecords(deviceId2)

        assertTrue("Ph found", phs.size == 2)
        assertTrue("Ph found", phs2.size == 1)

        val allPhs = repo.getAllPhRecords()
        assertTrue("Ph found", allPhs.size == 3)
    }

    @Test
    fun `add temperature record and get it`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"

        val recordedTemperature = repo.getTemperatureRecords(deviceId)
        assertTrue("Temperature found", recordedTemperature.isEmpty())

        repo.saveTemperatureRecord(deviceId, TemperatureRecord(generateRandomPh(), Instant.now()))

        val phs = repo.getTemperatureRecords(deviceId)
        assertTrue("Temperature found", phs.size == 1)
    }

    @Test
    fun `add 3 temperature records and get the list`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"

        val recordedTemperature = repo.getTemperatureRecords(deviceId)
        assertTrue("Temperature found", recordedTemperature.isEmpty())

        repo.saveTemperatureRecord(deviceId, TemperatureRecord(generateRandomPh(), Instant.now()))
        repo.saveTemperatureRecord(deviceId, TemperatureRecord(generateRandomPh(), Instant.now()))
        repo.saveTemperatureRecord(deviceId, TemperatureRecord(generateRandomPh(), Instant.now()))

        val phs = repo.getTemperatureRecords(deviceId)
        assertTrue("Temperature found", phs.size == 3)
    }

    @Test
    fun `add 2 temperature records to a device and 1 to another`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val deviceId2 = "80acf16c-d3bb-11ed-afa1-0242ac120003"

        repo.saveTemperatureRecord(deviceId, TemperatureRecord(generateRandomPh(), Instant.now()))
        repo.saveTemperatureRecord(deviceId, TemperatureRecord(generateRandomPh(), Instant.now()))
        repo.saveTemperatureRecord(deviceId2, TemperatureRecord(generateRandomPh(), Instant.now()))

        val phs = repo.getTemperatureRecords(deviceId)
        val phs2 = repo.getTemperatureRecords(deviceId2)

        assertTrue("Temperature found", phs.size == 2)
        assertTrue("Temperature found", phs2.size == 1)

        val allTemperatures = repo.getAllTemperatureRecords()
        assertTrue("Temperature found", allTemperatures.size == 3)
    }

    @Test
    fun `add 2 temperature records to a device and 1 ph record`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"

        repo.savePhRecord(deviceId, PhRecord(generateRandomPh(), Instant.now()))
        repo.savePhRecord(deviceId, PhRecord(generateRandomPh(), Instant.now()))
        repo.saveTemperatureRecord(deviceId, TemperatureRecord(generateRandomPh(), Instant.now()))

        val phs = repo.getPhRecords(deviceId)
        val temperatures = repo.getTemperatureRecords(deviceId)
        assertTrue("Ph found", phs.size == 2)
        assertTrue("Temperature found", temperatures.size == 1)
    }

    @Test
    fun `add 1 temperature record and 1 ph record to two devices`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val deviceId2 = "80acf16c-d3bb-11ed-afa1-0242ac120003"

        repo.savePhRecord(deviceId, PhRecord(generateRandomPh(), Instant.now()))
        repo.saveTemperatureRecord(deviceId, TemperatureRecord(generateRandomPh(), Instant.now()))
        repo.savePhRecord(deviceId2, PhRecord(generateRandomPh(), Instant.now()))
        repo.saveTemperatureRecord(deviceId2, TemperatureRecord(generateRandomPh(), Instant.now()))

        val phs1 = repo.getPhRecords(deviceId)
        val temperatures1 = repo.getTemperatureRecords(deviceId)
        val phs2 = repo.getPhRecords(deviceId2)
        val temperatures2 = repo.getTemperatureRecords(deviceId2)
        assertTrue("Ph found", phs1.size == 1)
        assertTrue( "Temperature found", temperatures1.size == 1)
        assertTrue("Ph found", phs2.size == 1)
        assertTrue("Temperature found", temperatures2.size == 1)
    }
}