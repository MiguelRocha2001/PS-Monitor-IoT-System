package pt.isel.iot_data_server.repo.time_series_repo

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertEquals
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.configuration.TSDBBuilder
import pt.isel.iot_data_server.domain.SensorRecord
import pt.isel.iot_data_server.repository.tsdb.SensorDataRepo
import pt.isel.iot_data_server.utils.generateRandomPh
import java.time.Instant

/**
 * @see: https://docs.influxdata.com/influxdb/cloud/write-data/best-practices/duplicate-points/
 */
class TsdbRepoDuplicateTests {
    private val tsdbBuilder: TSDBBuilder = TSDBBuilder("test")
    private val repo: SensorDataRepo = SensorDataRepo(
        tsdbBuilder.getClient(),
        tsdbBuilder.getBucket()
    )

    @BeforeEach
    fun deleteAll() {
        deleteAllSensorMeasurements(tsdbBuilder, "ph initial")
        deleteAllSensorMeasurements(tsdbBuilder, "temperature")
    }

    @Test
    fun `Inserting duplicate pH values in different timestamps`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val pHValue = generateRandomPh()

        val phsBefore = repo.getSensorRecords(deviceId, "ph initial")
        assertTrue("Should be empty", phsBefore.isEmpty())


        repo.saveSensorRecord(deviceId, SensorRecord("ph initial", pHValue, Instant.now()))

        val phsAfterFirstInsertion = repo.getSensorRecords(deviceId, "ph initial")
        assertTrue("Should be 1", phsAfterFirstInsertion.size == 1)

        Thread.sleep(50) // wait for the timestamp to be different

        repo.saveSensorRecord(deviceId, SensorRecord("ph initial", pHValue, Instant.now()))

        val phsAfterSecondInsertion = repo.getSensorRecords(deviceId, "ph initial")
        assertTrue("Should be 2", phsAfterSecondInsertion.size == 2)
        assertTrue("Should be equal", phsAfterFirstInsertion.all { it.value == pHValue })
    }

    @Test
    fun `Inserting duplicate sensor records in different devices`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val deviceId2 = "80acf16c-d3bb-11ed-afa1-0242ac120003"
        val pHValue = generateRandomPh()

        val phsBefore = repo.getSensorRecords(deviceId, "ph initial")
        assertTrue("Should be empty", phsBefore.isEmpty())

        repo.saveSensorRecord(deviceId, SensorRecord("ph initial", pHValue, Instant.now()))

        val phsAfterFirstInsertion = repo.getSensorRecords(deviceId, "ph initial")
        assertTrue("Should be 1", phsAfterFirstInsertion.size == 1)

        repo.saveSensorRecord(deviceId2, SensorRecord("ph initial", pHValue, Instant.now()))

        val phsAfterSecondInsertion = repo.getSensorRecords(deviceId, "ph initial")
        assertTrue("Should be 1", phsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", phsAfterFirstInsertion.all { it.value == pHValue })
    }

    @Test
    fun `Inserting different sensor records with the same timestamp`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val timestamp = Instant.now()

        val phValue = generateRandomPh()
        val phValue2 = generateRandomPh()

        assertTrue("Should be different", phValue != phValue2)

        val phsBefore = repo.getSensorRecords(deviceId, "ph initial")
        assertTrue("Should be empty", phsBefore.isEmpty())

        repo.saveSensorRecord(deviceId, SensorRecord("ph initial", phValue, timestamp))

        val phsAfterFirstInsertion = repo.getSensorRecords(deviceId, "ph initial")
        assertTrue("Should be 1", phsAfterFirstInsertion.size == 1)

        repo.saveSensorRecord(deviceId, SensorRecord("ph initial", phValue2, timestamp))

        val phsAfterSecondInsertion = repo.getSensorRecords(deviceId, "ph initial")
        assertTrue("Should be 1", phsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", phsAfterSecondInsertion.all { it.value == phValue2 })
    }

    @Test
    fun `Inserting the same sensor record with the same timestamp`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val timestamp = Instant.now()
        val phValue = generateRandomPh()

        val phsBefore = repo.getSensorRecords(deviceId, "ph initial")
        assertTrue("Should be empty", phsBefore.isEmpty())

        repo.saveSensorRecord(deviceId, SensorRecord("ph initial", phValue, timestamp))

        val phsAfterFirstInsertion = repo.getSensorRecords(deviceId, "ph initial")
        assertTrue("Should be 1", phsAfterFirstInsertion.size == 1)

        repo.saveSensorRecord(deviceId, SensorRecord("ph initial", phValue, timestamp))

        val phsAfterSecondInsertion = repo.getSensorRecords(deviceId, "ph initial")
        assertTrue("Should be 1", phsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", phsAfterSecondInsertion.all { it.value == phValue })
    }

    @Test
    fun `Inserting the same sensor records to different devices`() {
        val device1Id = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val device2Id = "80acf16c-d3bb-11ed-afa1-0242ac120003"
        val phValue = generateRandomPh()

        val device1PhsBefore = repo.getSensorRecords(device1Id, "ph initial")
        assertTrue("Should be empty", device1PhsBefore.isEmpty())

        val device2PhsBefore = repo.getSensorRecords(device2Id, "ph initial")
        assertTrue("Should be empty", device2PhsBefore.isEmpty())

        repo.saveSensorRecord(device1Id, SensorRecord("ph initial", phValue, Instant.now()))

        val device1PhsAfterFirstInsertion = repo.getSensorRecords(device1Id, "ph initial")
        assertTrue("Should be 1", device1PhsAfterFirstInsertion.size == 1)

        val device2PhsAfterFirstInsertion = repo.getSensorRecords(device2Id, "ph initial")
        assertTrue("Should be empty", device2PhsAfterFirstInsertion.isEmpty())

        repo.saveSensorRecord(device2Id, SensorRecord("ph initial", phValue, Instant.now()))

        val device1PhsAfterSecondInsertion = repo.getSensorRecords(device1Id, "ph initial")
        assertTrue("Should be 1", device1PhsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", device1PhsAfterSecondInsertion.all { it.value == phValue })

        val device2PhsAfterSecondInsertion = repo.getSensorRecords(device2Id, "ph initial")
        assertTrue("Should be 1", device2PhsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", device2PhsAfterSecondInsertion.all { it.value == phValue })
    }

    @Test
    fun `Inserting sensor records, with the same timestamp, to different devices`() {
        val device1Id = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val device2Id = "80acf16c-d3bb-11ed-afa1-0242ac120003"
        val device1pHValue = generateRandomPh()
        val device2pHValue = generateRandomPh()
        val timestamp = Instant.now()

        val device1PhsBefore = repo.getSensorRecords(device1Id, "ph initial")
        assertTrue("Should be empty", device1PhsBefore.isEmpty())

        val device2PhsBefore = repo.getSensorRecords(device2Id, "ph initial")
        assertTrue("Should be empty", device2PhsBefore.isEmpty())

        repo.saveSensorRecord(device1Id, SensorRecord("ph initial", device1pHValue, timestamp))

        val device1PhsAfterFirstInsertion = repo.getSensorRecords(device1Id, "ph initial")
        assertEquals("Should be 1", 1, device1PhsAfterFirstInsertion.size)

        val device2PhsAfterFirstInsertion = repo.getSensorRecords(device2Id, "ph initial")
        assertTrue("Should be empty", device2PhsAfterFirstInsertion.isEmpty())

        repo.saveSensorRecord(device2Id, SensorRecord("ph initial", device2pHValue, timestamp))

        val device1PhsAfterSecondInsertion = repo.getSensorRecords(device1Id, "ph initial")
        assertEquals("Should be 1", 1, device1PhsAfterSecondInsertion.size)
        assertEquals("Should be equal", device1pHValue, device1PhsAfterSecondInsertion.first().value)

        val device2PhsAfterSecondInsertion = repo.getSensorRecords(device2Id, "ph initial")
        assertEquals("Should be 1", 1, device2PhsAfterSecondInsertion.size)
        assertEquals("Should be equal", device2pHValue, device2PhsAfterSecondInsertion.first().value)
    }
}