package pt.isel.iot_data_server.repo.time_series

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertEquals
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.configuration.TSDBBuilder
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.domain.TemperatureRecord
import pt.isel.iot_data_server.repository.tsdb.TSDBRepository
import pt.isel.iot_data_server.utils.generateRandomPh
import pt.isel.iot_data_server.utils.generateRandomTemperature
import java.time.Instant

/**
 * @see: https://docs.influxdata.com/influxdb/cloud/write-data/best-practices/duplicate-points/
 */
class TsdbRepoDuplicateTests {
    private val tsdbBuilder: TSDBBuilder = TSDBBuilder("test_bucket")
    private val repo: TSDBRepository = TSDBRepository(
        tsdbBuilder.getClient(),
        tsdbBuilder.getBucket()
    )

    @AfterEach
    fun deleteAll() {
        deleteAllPhMeasurements(tsdbBuilder)
        deleteAllTemperatureMeasurements(tsdbBuilder)
    }

    @Test
    fun `Inserting duplicate pH values in different timestamps`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val pHValue = generateRandomPh()

        val phsBefore = repo.getPhRecords(deviceId)
        assertTrue("Should be empty", phsBefore.isEmpty())

        repo.savePhRecord(deviceId, PhRecord(pHValue, Instant.now()))

        val phsAfterFirstInsertion = repo.getPhRecords(deviceId)
        assertTrue("Should be 1", phsAfterFirstInsertion.size == 1)

        Thread.sleep(50) // wait for the timestamp to be different

        repo.savePhRecord(deviceId, PhRecord(pHValue, Instant.now()))

        val phsAfterSecondInsertion = repo.getPhRecords(deviceId)
        assertTrue("Should be 2", phsAfterSecondInsertion.size == 2)
        assertTrue("Should be equal", phsAfterFirstInsertion.all { it.value == pHValue })
    }

    @Test
    fun `Inserting duplicate temperature values in different timestamps`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val temperatureValue = generateRandomTemperature()

        val temperaturesBefore = repo.getTemperatureRecords(deviceId)
        assertTrue("Should be empty", temperaturesBefore.isEmpty())

        repo.saveTemperatureRecord(deviceId, TemperatureRecord(temperatureValue, Instant.now()))

        val temperaturesAfterFirstInsertion = repo.getTemperatureRecords(deviceId)
        assertTrue("Should be 1", temperaturesAfterFirstInsertion.size == 1)

        Thread.sleep(50) // wait for the timestamp to be different

        repo.saveTemperatureRecord(deviceId, TemperatureRecord(temperatureValue, Instant.now()))

        val temperaturesAfterSecondInsertion = repo.getTemperatureRecords(deviceId)
        assertTrue("Should be 2", temperaturesAfterSecondInsertion.size == 2)
        assertTrue("Should be equal", temperaturesAfterFirstInsertion.all { it.value == temperatureValue })
    }

    @Test
    fun `Inserting duplicate pH values in different devices`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val deviceId2 = "80acf16c-d3bb-11ed-afa1-0242ac120003"
        val pHValue = generateRandomPh()

        val phsBefore = repo.getPhRecords(deviceId)
        assertTrue("Should be empty", phsBefore.isEmpty())

        repo.savePhRecord(deviceId, PhRecord(pHValue, Instant.now()))

        val phsAfterFirstInsertion = repo.getPhRecords(deviceId)
        assertTrue("Should be 1", phsAfterFirstInsertion.size == 1)

        repo.savePhRecord(deviceId2, PhRecord(pHValue, Instant.now()))

        val phsAfterSecondInsertion = repo.getPhRecords(deviceId)
        assertTrue("Should be 1", phsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", phsAfterFirstInsertion.all { it.value == pHValue })
    }

    @Test
    fun `Inserting duplicate temperature values in different devices`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val deviceId2 = "80acf16c-d3bb-11ed-afa1-0242ac120003"
        val tempValue = generateRandomTemperature()

        val tempsBefore = repo.getTemperatureRecords(deviceId)
        assertTrue("Should be empty", tempsBefore.isEmpty())

        repo.saveTemperatureRecord(deviceId, TemperatureRecord(tempValue, Instant.now()))

        val tempsAfterFirstInsertion = repo.getTemperatureRecords(deviceId)
        assertTrue("Should be 1", tempsAfterFirstInsertion.size == 1)

        repo.saveTemperatureRecord(deviceId2, TemperatureRecord(tempValue, Instant.now()))

        val tempsAfterSecondInsertion = repo.getTemperatureRecords(deviceId)
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

        val phsBefore = repo.getPhRecords(deviceId)
        assertTrue("Should be empty", phsBefore.isEmpty())

        repo.savePhRecord(deviceId, PhRecord(phValue, timestamp))

        val phsAfterFirstInsertion = repo.getPhRecords(deviceId)
        assertTrue("Should be 1", phsAfterFirstInsertion.size == 1)

        repo.savePhRecord(deviceId, PhRecord(phValue2, timestamp))

        val phsAfterSecondInsertion = repo.getPhRecords(deviceId)
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

        val tempsBefore = repo.getTemperatureRecords(deviceId)
        assertTrue("Should be empty", tempsBefore.isEmpty())

        repo.saveTemperatureRecord(deviceId, TemperatureRecord(tempValue, timestamp))

        val tempsAfterFirstInsertion = repo.getTemperatureRecords(deviceId)
        assertTrue("Should be 1", tempsAfterFirstInsertion.size == 1)

        repo.saveTemperatureRecord(deviceId, TemperatureRecord(tempValue2, timestamp))

        val tempsAfterSecondInsertion = repo.getTemperatureRecords(deviceId)
        assertTrue("Should be 1", tempsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", tempsAfterSecondInsertion.all { it.value == tempValue2 })
    }

    @Test
    fun `Inserting the same pH value with the same timestamp`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val timestamp = Instant.now()
        val phValue = generateRandomPh()

        val phsBefore = repo.getPhRecords(deviceId)
        assertTrue("Should be empty", phsBefore.isEmpty())

        repo.savePhRecord(deviceId, PhRecord(phValue, timestamp))

        val phsAfterFirstInsertion = repo.getPhRecords(deviceId)
        assertTrue("Should be 1", phsAfterFirstInsertion.size == 1)

        repo.savePhRecord(deviceId, PhRecord(phValue, timestamp))

        val phsAfterSecondInsertion = repo.getPhRecords(deviceId)
        assertTrue("Should be 1", phsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", phsAfterSecondInsertion.all { it.value == phValue })
    }

    @Test
    fun `Inserting the same temperature value with the same timestamp`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val timestamp = Instant.now()
        val tempValue = generateRandomTemperature()

        val tempsBefore = repo.getTemperatureRecords(deviceId)
        assertTrue("Should be empty", tempsBefore.isEmpty())

        repo.saveTemperatureRecord(deviceId, TemperatureRecord(tempValue, timestamp))

        val tempsAfterFirstInsertion = repo.getTemperatureRecords(deviceId)
        assertTrue("Should be 1", tempsAfterFirstInsertion.size == 1)

        repo.saveTemperatureRecord(deviceId, TemperatureRecord(tempValue, timestamp))

        val tempsAfterSecondInsertion = repo.getTemperatureRecords(deviceId)
        assertTrue("Should be 1", tempsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", tempsAfterSecondInsertion.all { it.value == tempValue })
    }

    @Test
    fun `Inserting the same pH values to different devices`() {
        val device1Id = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val device2Id = "80acf16c-d3bb-11ed-afa1-0242ac120003"
        val phValue = generateRandomPh()

        val device1PhsBefore = repo.getPhRecords(device1Id)
        assertTrue("Should be empty", device1PhsBefore.isEmpty())

        val device2PhsBefore = repo.getPhRecords(device2Id)
        assertTrue("Should be empty", device2PhsBefore.isEmpty())

        repo.savePhRecord(device1Id, PhRecord(phValue, Instant.now()))

        val device1PhsAfterFirstInsertion = repo.getPhRecords(device1Id)
        assertTrue("Should be 1", device1PhsAfterFirstInsertion.size == 1)

        val device2PhsAfterFirstInsertion = repo.getPhRecords(device2Id)
        assertTrue("Should be empty", device2PhsAfterFirstInsertion.isEmpty())

        repo.savePhRecord(device2Id, PhRecord(phValue, Instant.now()))

        val device1PhsAfterSecondInsertion = repo.getPhRecords(device1Id)
        assertTrue("Should be 1", device1PhsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", device1PhsAfterSecondInsertion.all { it.value == phValue })

        val device2PhsAfterSecondInsertion = repo.getPhRecords(device2Id)
        assertTrue("Should be 1", device2PhsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", device2PhsAfterSecondInsertion.all { it.value == phValue })
    }

    @Test
    fun `Inserting the same temperature values to different devices`() {
        val device1Id = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val device2Id = "80acf16c-d3bb-11ed-afa1-0242ac120003"
        val tempValue = generateRandomTemperature()

        val device1TempsBefore = repo.getTemperatureRecords(device1Id)
        assertTrue("Should be empty", device1TempsBefore.isEmpty())

        val device2TempsBefore = repo.getTemperatureRecords(device2Id)
        assertTrue("Should be empty", device2TempsBefore.isEmpty())

        repo.saveTemperatureRecord(device1Id, TemperatureRecord(tempValue, Instant.now()))

        val device1TempsAfterFirstInsertion = repo.getTemperatureRecords(device1Id)
        assertTrue("Should be 1", device1TempsAfterFirstInsertion.size == 1)

        val device2TempsAfterFirstInsertion = repo.getTemperatureRecords(device2Id)
        assertTrue("Should be empty", device2TempsAfterFirstInsertion.isEmpty())

        repo.saveTemperatureRecord(device2Id, TemperatureRecord(tempValue, Instant.now()))

        val device1TempsAfterSecondInsertion = repo.getTemperatureRecords(device1Id)
        assertTrue("Should be 1", device1TempsAfterSecondInsertion.size == 1)
        assertTrue("Should be equal", device1TempsAfterSecondInsertion.all { it.value == tempValue })

        val device2TempsAfterSecondInsertion = repo.getTemperatureRecords(device2Id)
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

        val device1PhsBefore = repo.getPhRecords(device1Id)
        assertTrue("Should be empty", device1PhsBefore.isEmpty())

        val device2PhsBefore = repo.getPhRecords(device2Id)
        assertTrue("Should be empty", device2PhsBefore.isEmpty())

        repo.savePhRecord(device1Id, PhRecord(device1pHValue, timestamp))

        val device1PhsAfterFirstInsertion = repo.getPhRecords(device1Id)
        assertEquals("Should be 1", 1, device1PhsAfterFirstInsertion.size)

        val device2PhsAfterFirstInsertion = repo.getPhRecords(device2Id)
        assertTrue("Should be empty", device2PhsAfterFirstInsertion.isEmpty())

        repo.savePhRecord(device2Id, PhRecord(device2pHValue, timestamp))

        val device1PhsAfterSecondInsertion = repo.getPhRecords(device1Id)
        assertEquals("Should be 1", 1, device1PhsAfterSecondInsertion.size)
        assertEquals("Should be equal", device1pHValue, device1PhsAfterSecondInsertion.first().value)

        val device2PhsAfterSecondInsertion = repo.getPhRecords(device2Id)
        assertEquals("Should be 1", 1, device2PhsAfterSecondInsertion.size)
        assertEquals("Should be equal", device2pHValue, device2PhsAfterSecondInsertion.first().value)
    }

    @Test
    fun `Inserting temperature values, with the same timestamp, to different devices`() {
        val device1Id = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val device2Id = "80acf16c-d3bb-11ed-afa1-0242ac120003"
        val device1TempValue = generateRandomTemperature()
        val device2TempValue = generateRandomTemperature()
        val timestamp = Instant.now()

        val device1TempsBefore = repo.getTemperatureRecords(device1Id)
        assertTrue("Should be empty", device1TempsBefore.isEmpty())

        val device2TempsBefore = repo.getTemperatureRecords(device2Id)
        assertTrue("Should be empty", device2TempsBefore.isEmpty())

        repo.saveTemperatureRecord(device1Id, TemperatureRecord(device1TempValue, timestamp))

        val device1TempsAfterFirstInsertion = repo.getTemperatureRecords(device1Id)
        assertEquals("Should be 1", 1, device1TempsAfterFirstInsertion.size)

        val device2TempsAfterFirstInsertion = repo.getTemperatureRecords(device2Id)
        assertTrue("Should be empty", device2TempsAfterFirstInsertion.isEmpty())

        repo.saveTemperatureRecord(device2Id, TemperatureRecord(device2TempValue, timestamp))

        val device1TempsAfterSecondInsertion = repo.getTemperatureRecords(device1Id)
        assertEquals("Should be 1", 1, device1TempsAfterSecondInsertion.size)
        assertEquals("Should be equal", device1TempValue, device1TempsAfterSecondInsertion.first().value)

        val device2TempsAfterSecondInsertion = repo.getTemperatureRecords(device2Id)
        assertEquals("Should be 1", 1, device2TempsAfterSecondInsertion.size)
        assertEquals("Should be equal", device2TempValue, device2TempsAfterSecondInsertion.first().value)
    }

    @Test
    fun `Inserting the same pH value, with the same timestamp, to different devices`() {
        val device1Id = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val device2Id = "80acf16c-d3bb-11ed-afa1-0242ac120003"
        val phValue = generateRandomPh()
        val timestamp = Instant.now()

        val device1PhsBefore = repo.getPhRecords(device1Id)
        assertTrue("Should be empty", device1PhsBefore.isEmpty())

        val device2PhsBefore = repo.getPhRecords(device2Id)
        assertTrue("Should be empty", device2PhsBefore.isEmpty())

        repo.savePhRecord(device1Id, PhRecord(phValue, timestamp))

        val device1PhsAfterFirstInsertion = repo.getPhRecords(device1Id)
        assertEquals("Should be 1", 1, device1PhsAfterFirstInsertion.size)

        val device2PhsAfterFirstInsertion = repo.getPhRecords(device2Id)
        assertTrue("Should be empty", device2PhsAfterFirstInsertion.isEmpty())

        repo.savePhRecord(device2Id, PhRecord(phValue, timestamp))

        val device1PhsAfterSecondInsertion = repo.getPhRecords(device1Id)
        assertEquals("Should be 1", 1, device1PhsAfterSecondInsertion.size)
        assertEquals("Should be equal", phValue, device1PhsAfterSecondInsertion.first().value)

        val device2PhsAfterSecondInsertion = repo.getPhRecords(device2Id)
        assertEquals("Should be 1", 1, device2PhsAfterSecondInsertion.size)
        assertEquals("Should be equal", phValue, device2PhsAfterSecondInsertion.first().value)
    }

    @Test
    fun `Inserting the same temperature value, with the same timestamp, to different devices`() {
        val device1Id = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val device2Id = "80acf16c-d3bb-11ed-afa1-0242ac120003"
        val tempValue = generateRandomTemperature()
        val timestamp = Instant.now()

        val device1TempsBefore = repo.getTemperatureRecords(device1Id)
        assertTrue("Should be empty", device1TempsBefore.isEmpty())

        val device2TempsBefore = repo.getTemperatureRecords(device2Id)
        assertTrue("Should be empty", device2TempsBefore.isEmpty())

        repo.saveTemperatureRecord(device1Id, TemperatureRecord(tempValue, timestamp))

        val device1TempsAfterFirstInsertion = repo.getTemperatureRecords(device1Id)
        assertEquals("Should be 1", 1, device1TempsAfterFirstInsertion.size)

        val device2TempsAfterFirstInsertion = repo.getTemperatureRecords(device2Id)
        assertTrue("Should be empty", device2TempsAfterFirstInsertion.isEmpty())

        repo.saveTemperatureRecord(device2Id, TemperatureRecord(tempValue, timestamp))

        val device1TempsAfterSecondInsertion = repo.getTemperatureRecords(device1Id)
        assertEquals("Should be 1", 1, device1TempsAfterSecondInsertion.size)
        assertEquals("Should be equal", tempValue, device1TempsAfterSecondInsertion.first().value)

        val device2TempsAfterSecondInsertion = repo.getTemperatureRecords(device2Id)
        assertEquals("Should be 1", 1, device2TempsAfterSecondInsertion.size)
        assertEquals("Should be equal", tempValue, device2TempsAfterSecondInsertion.first().value)
    }
}