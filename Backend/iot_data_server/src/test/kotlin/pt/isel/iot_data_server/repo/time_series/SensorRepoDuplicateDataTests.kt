package pt.isel.iot_data_server.repo.time_series

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertEquals
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.configuration.TSDBBuilder
import pt.isel.iot_data_server.domain.SensorRecord
import pt.isel.iot_data_server.repository.tsdb.SensorDataRepo
import pt.isel.iot_data_server.utils.generateRandomPh
import pt.isel.iot_data_server.utils.generateRandomTemperature
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

    @AfterEach
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
    fun `Inserting duplicate pH values in different devices`() {
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
    fun `Inserting duplicate temperature values in different devices`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val deviceId2 = "80acf16c-d3bb-11ed-afa1-0242ac120003"
        val tempValue = generateRandomTemperature()

        val tempsBefore = repo.getSensorRecords(deviceId, "temperature")
        assertTrue("Should be empty", tempsBefore.isEmpty())

        repo.saveSensorRecord(deviceId, SensorRecord("temperature", tempValue, Instant.now()))

        val tempsAfterFirstInsertion = repo.getSensorRecords(deviceId, "temperature")
        assertTrue("Should be 1", tempsAfterFirstInsertion.size == 1)

        repo.saveSensorRecord(deviceId2, SensorRecord("temperature", tempValue, Instant.now()))

        val tempsAfterSecondInsertion = repo.getSensorRecords(deviceId, "temperature")
        assertTrue("Should be 1", tempsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", tempsAfterFirstInsertion.all { it.value == tempValue })
    }


    @Test
    fun `Inserting different pH values with the same timestamp`() {
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
    fun `Inserting different temperature values with the same timestamp`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val timestamp = Instant.now()

        val tempValue = generateRandomTemperature()
        val tempValue2 = generateRandomTemperature()

        assertTrue("Should be different", tempValue != tempValue2)

        val tempsBefore = repo.getSensorRecords(deviceId, "temperature")
        assertTrue("Should be empty", tempsBefore.isEmpty())

        repo.saveSensorRecord(deviceId, SensorRecord("temperature", tempValue, timestamp))

        val tempsAfterFirstInsertion = repo.getSensorRecords(deviceId, "temperature")
        assertTrue("Should be 1", tempsAfterFirstInsertion.size == 1)

        repo.saveSensorRecord(deviceId, SensorRecord("temperature", tempValue2, timestamp))

        val tempsAfterSecondInsertion = repo.getSensorRecords(deviceId, "temperature")
        assertTrue("Should be 1", tempsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", tempsAfterSecondInsertion.all { it.value == tempValue2 })
    }

    @Test
    fun `Inserting the same pH value with the same timestamp`() {
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
    fun `Inserting the same temperature value with the same timestamp`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val timestamp = Instant.now()
        val tempValue = generateRandomTemperature()

        val tempsBefore = repo.getSensorRecords(deviceId, "temperature")
        assertTrue("Should be empty", tempsBefore.isEmpty())

        repo.saveSensorRecord(deviceId, SensorRecord("temperature", tempValue, timestamp))

        val tempsAfterFirstInsertion = repo.getSensorRecords(deviceId, "temperature")
        assertTrue("Should be 1", tempsAfterFirstInsertion.size == 1)

        repo.saveSensorRecord(deviceId, SensorRecord("temperature", tempValue, timestamp))

        val tempsAfterSecondInsertion = repo.getSensorRecords(deviceId, "temperature")
        assertTrue("Should be 1", tempsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", tempsAfterSecondInsertion.all { it.value == tempValue })
    }

    @Test
    fun `Inserting the same pH values to different devices`() {
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
    fun `Inserting the same temperature values to different devices`() {
        val device1Id = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val device2Id = "80acf16c-d3bb-11ed-afa1-0242ac120003"
        val tempValue = generateRandomTemperature()

        val device1TempsBefore = repo.getSensorRecords(device1Id, "temperature")
        assertTrue("Should be empty", device1TempsBefore.isEmpty())

        val device2TempsBefore = repo.getSensorRecords(device2Id, "temperature")
        assertTrue("Should be empty", device2TempsBefore.isEmpty())

        repo.saveSensorRecord(device1Id, SensorRecord("temperature", tempValue, Instant.now()))

        val device1TempsAfterFirstInsertion = repo.getSensorRecords(device1Id, "temperature")
        assertTrue("Should be 1", device1TempsAfterFirstInsertion.size == 1)

        val device2TempsAfterFirstInsertion = repo.getSensorRecords(device2Id, "temperature")
        assertTrue("Should be empty", device2TempsAfterFirstInsertion.isEmpty())

        repo.saveSensorRecord(device2Id, SensorRecord("temperature", tempValue, Instant.now()))

        val device1TempsAfterSecondInsertion = repo.getSensorRecords(device1Id, "temperature")
        assertTrue("Should be 1", device1TempsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", device1TempsAfterSecondInsertion.all { it.value == tempValue })

        val device2TempsAfterSecondInsertion = repo.getSensorRecords(device2Id, "temperature")
        assertTrue("Should be 1", device2TempsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", device2TempsAfterSecondInsertion.all { it.value == tempValue })
    }

    @Test
    fun `Inserting pH values, with the same timestamp, to different devices`() {
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


    @Test
    fun `Inserting the same temperature value, with the same timestamp, to different devices`() {
        val device1Id = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val device2Id = "80acf16c-d3bb-11ed-afa1-0242ac120003"
        val tempValue = generateRandomTemperature()
        val timestamp = Instant.now()

        val device1TempsBefore = repo.getSensorRecords(device1Id, "temperature")
        assertTrue("Should be empty", device1TempsBefore.isEmpty())

        val device2TempsBefore = repo.getSensorRecords(device2Id, "temperature")
        assertTrue("Should be empty", device2TempsBefore.isEmpty())

        repo.saveSensorRecord(device1Id, SensorRecord("temperature", tempValue, timestamp))

        val device1TempsAfterFirstInsertion = repo.getSensorRecords(device1Id, "temperature")
        assertEquals("Should be 1", 1, device1TempsAfterFirstInsertion.size)

        val device2TempsAfterFirstInsertion = repo.getSensorRecords(device2Id, "temperature")
        assertTrue("Should be empty", device2TempsAfterFirstInsertion.isEmpty())

        repo.saveSensorRecord(device2Id, SensorRecord("temperature", tempValue, timestamp))

        val device1TempsAfterSecondInsertion = repo.getSensorRecords(device1Id, "temperature")
        assertEquals("Should be 1", 1, device1TempsAfterSecondInsertion.size)
        assertEquals("Should be equal", tempValue, device1TempsAfterSecondInsertion.first().value)

        val device2TempsAfterSecondInsertion = repo.getSensorRecords(device2Id, "temperature")
        assertEquals("Should be 1", 1, device2TempsAfterSecondInsertion.size)
        assertEquals("Should be equal", tempValue, device2TempsAfterSecondInsertion.first().value)
    }
}