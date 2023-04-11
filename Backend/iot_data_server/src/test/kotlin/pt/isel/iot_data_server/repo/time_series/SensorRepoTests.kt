package pt.isel.iot_data_server.repo.time_series

import deleteAllPhMeasurements
import deleteAllTemperatureMeasurements
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.domain.TemperatureRecord
import pt.isel.iot_data_server.repository.tsdb.TSDBConfig
import pt.isel.iot_data_server.repository.tsdb.TSDBRepository
import pt.isel.iot_data_server.utils.generateRandomPh
import pt.isel.iot_data_server.utils.getRandomInstantWithinLastWeek
import java.time.Instant
import kotlin.concurrent.thread

//TEST THE INFLUXDB
class TsdbRepoTests {

    @AfterEach
    fun deleteAll() {
        val testDBConfig = TSDBConfig().tsdb2Properties()
        deleteAllPhMeasurements(testDBConfig)
        deleteAllTemperatureMeasurements(testDBConfig)
    }

    @Test
    fun `add ph record and get it`() {
        val testDBConfig = TSDBConfig().tsdb2Properties()
        val repo = TSDBRepository(testDBConfig)
        val deviceId = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120002")
        val phsBefore = repo.getPhRecords(deviceId)
        assertTrue("Ph found", phsBefore.isEmpty())
        repo.savePhRecord(deviceId, PhRecord(generateRandomPh(), Instant.now()))
        val phs = repo.getPhRecords(deviceId)
        // assertTrue("Ph found", phs.size == 1)
        assertTrue("Ph found", phs.size == 1)
    }

    @Test
    fun `add 3 ph records and get the list`() {
        val testDBConfig = TSDBConfig().tsdb2Properties()
        val repo = TSDBRepository(testDBConfig)
        val deviceId = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120002")
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
        val testDBConfig = TSDBConfig().tsdb2Properties()
        val repo = TSDBRepository(testDBConfig)
        val deviceId = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120002")
        val deviceId2 = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120003")
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
        val testDBConfig = TSDBConfig().tsdb2Properties()
        val repo = TSDBRepository(testDBConfig)
        val deviceId = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120002")
        val recordedTemperature = repo.getTemperatureRecords(deviceId)
        assertTrue("Temperature found", recordedTemperature.isEmpty())
        repo.saveTemperatureRecord(deviceId, TemperatureRecord(generateRandomPh(), Instant.now()))
        val phs = repo.getTemperatureRecords(deviceId)
        assertTrue("Temperature found", phs.size == 1)
    }

    @Test
    fun `add 3 temperature records and get the list`() {
        val testDBConfig = TSDBConfig().tsdb2Properties()
        val repo = TSDBRepository(testDBConfig)
        val deviceId = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120002")
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
        val testDBConfig = TSDBConfig().tsdb2Properties()
        val repo = TSDBRepository(testDBConfig)
        val deviceId = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120002")
        val deviceId2 = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120003")
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
        val testDBConfig = TSDBConfig().tsdb2Properties()
        val repo = TSDBRepository(testDBConfig)
        val deviceId = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120002")
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
        val testDBConfig = TSDBConfig().tsdb2Properties()
        val repo = TSDBRepository(testDBConfig)
        val deviceId = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120002")
        val deviceId2 = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120003")
        repo.savePhRecord(deviceId, PhRecord(generateRandomPh(), Instant.now()))
        repo.saveTemperatureRecord(deviceId, TemperatureRecord(generateRandomPh(), Instant.now()))
        repo.savePhRecord(deviceId2, PhRecord(generateRandomPh(), Instant.now()))
        repo.saveTemperatureRecord(deviceId2, TemperatureRecord(generateRandomPh(), Instant.now()))
        val phs1 = repo.getPhRecords(deviceId)
        val temperatures1 = repo.getTemperatureRecords(deviceId)
        val phs2 = repo.getPhRecords(deviceId2)
        val temperatures2 = repo.getTemperatureRecords(deviceId2)
        assertTrue("Ph found", phs1.size == 1)
        assertTrue("Temperature found", temperatures1.size == 1)
        assertTrue("Ph found", phs2.size == 1)
        assertTrue("Temperature found", temperatures2.size == 1)
    }

    //FIXME:THESE TESTS NEED FIXING
    @Test
    fun `add ph record and get it concurrently`(){ //THIS TEST CAN FAIL IF TWO INSTANTS ARE GENERATED ARE EQUAL (HIGHLY UNLIKELY)
        val testDBConfig = TSDBConfig().tsdb2Properties()
        val repo = TSDBRepository(testDBConfig)
        val deviceId = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120002")

        // Number of threads for concurrent execution
        val numThreads = 10
        val repetitions = 100

        // Start multiple threads to concurrently add ph records
        val threads = List(numThreads) {
            thread(start = true) {
                repeat(repetitions){
                    // generate random instant
                    val ph = generateRandomPh()
                    val randomInstant = getRandomInstantWithinLastWeek()
                    repo.savePhRecord(deviceId, PhRecord(ph, randomInstant))
                    }
                }
            }

        // Wait for all threads to complete
        threads.forEach { it.join() }

        // Get ph records after concurrent execution
        val phs = repo.getPhRecords(deviceId)

        // Assert that only one ph record is added

        assertEquals(phs.size, numThreads * repetitions)
    }

    @Test
    fun `add temperature record and get it concurrently`(){ //THIS TEST CAN FAIL IF TWO INSTANTS ARE GENERATED ARE EQUAL (HIGHLY UNLIKELY)
        val testDBConfig = TSDBConfig().tsdb2Properties()
        val repo = TSDBRepository(testDBConfig)
        val deviceId = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120002")

        // Number of threads for concurrent execution
        val numThreads = 10
        val repetitions = 100

        // Start multiple threads to concurrently add ph records
        val threads = List(numThreads) {
            thread(start = true) {
                repeat(repetitions){
                    // generate random instant
                    val temperature = generateRandomPh()
                    val randomInstant = getRandomInstantWithinLastWeek()
                    repo.saveTemperatureRecord(deviceId, TemperatureRecord(temperature, randomInstant))
                }
            }
        }

        // Wait for all threads to complete
        threads.forEach { it.join() }

        // Get ph records after concurrent execution
        val temperatures = repo.getTemperatureRecords(deviceId)

        // Assert that only one ph record is added

        assertEquals(temperatures.size, numThreads * repetitions)
    }

    @Test
    fun `add ph and temperature concurrently to multiple devices`(){
        val testDBConfig = TSDBConfig().tsdb2Properties()
        val repo = TSDBRepository(testDBConfig)


        // Number of threads for concurrent execution
        val numThreads = 10
        val repetitions = 100

        // Start multiple threads to concurrently add ph records
        val threads = List(numThreads) {
            thread(start = true) {
                repeat(repetitions){
                    // generate random instant
                    val deviceId = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120002")
                    val ph = generateRandomPh()
                    val temperature = generateRandomPh()
                    val randomInstant = getRandomInstantWithinLastWeek()
                    repo.savePhRecord(deviceId, PhRecord(ph, randomInstant))
                    repo.saveTemperatureRecord(deviceId, TemperatureRecord(temperature, randomInstant))
                }
            }
        }

        // Wait for all threads to complete
        threads.forEach { it.join() }

        // Get ph records after concurrent execution
        val phs = repo.getAllPhRecords()
        val temperatures = repo.getAllTemperatureRecords()

        // Assert that only one ph record is added

        assertEquals(phs.size, numThreads * repetitions)
        assertEquals(temperatures.size, numThreads * repetitions)
    }


}