package pt.isel.iot_data_server.repo

import deleteAllPhMeasurements
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.repository.tsdb.TSDBConfig
import pt.isel.iot_data_server.repository.tsdb.TSDBRepository
import java.time.Instant
import java.util.*

//TEST THE INFLUXDB
class TsdbRepoTests {

    @AfterEach
    fun deleteAll() {
            val testDBConfig = TSDBConfig().tsdb2Properties()
            deleteAllPhMeasurements(testDBConfig)
        }

    @Test
    fun `add ph record and get it`() {
        val testDBConfig = TSDBConfig().tsdb2Properties()
        val repo = TSDBRepository(testDBConfig)
        val deviceId = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120002")
        val phsBefore = repo.getPhRecords(deviceId)
        assertTrue("Ph found", phsBefore.isEmpty())
        repo.savePhRecord(deviceId, PhRecord(generateRandomDouble(), Instant.now()))
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
        repo.savePhRecord(deviceId, PhRecord(generateRandomDouble(), Instant.now()))
        repo.savePhRecord(deviceId, PhRecord(generateRandomDouble(), Instant.now()))
        repo.savePhRecord(deviceId, PhRecord(generateRandomDouble(), Instant.now()))
        val phs = repo.getPhRecords(deviceId)
        assertTrue("Ph found", phs.size == 3)
    }

    @Test
    fun `add 2 ph records to a device and 1 to another`() {
        val testDBConfig = TSDBConfig().tsdb2Properties()
        val repo = TSDBRepository(testDBConfig)
        val deviceId = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120002")
        val deviceId2 = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120003")
        repo.savePhRecord(deviceId, PhRecord(generateRandomDouble(), Instant.now()))
        repo.savePhRecord(deviceId, PhRecord(generateRandomDouble(), Instant.now()))
        repo.savePhRecord(deviceId2, PhRecord(generateRandomDouble(), Instant.now()))
        val phs = repo.getPhRecords(deviceId)
        val phs2 = repo.getPhRecords(deviceId2)
        assertTrue("Ph found", phs.size == 2)
        assertTrue("Ph found", phs2.size == 1)
    }

    @Test
    fun `add temperature record and get it`() {
        val testDBConfig = TSDBConfig().tsdb2Properties()
        val repo = TSDBRepository(testDBConfig)
        val deviceId = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120002")
        val phsBefore = repo.getPhRecords(deviceId)
        assertTrue("Ph found", phsBefore.isEmpty())
        repo.savePhRecord(deviceId, PhRecord(generateRandomDouble(), Instant.now()))
        val phs = repo.getPhRecords(deviceId)
        // assertTrue("Ph found", phs.size == 1)
        assertTrue("Temperature found", phs.size == 1)
    }

    @Test
    fun `add 3 temperature records and get the list`() {
        val testDBConfig = TSDBConfig().tsdb2Properties()
        val repo = TSDBRepository(testDBConfig)
        val deviceId = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120002")
        val phsBefore = repo.getPhRecords(deviceId)
        assertTrue("Ph found", phsBefore.isEmpty())
        repo.savePhRecord(deviceId, PhRecord(generateRandomDouble(), Instant.now()))
        repo.savePhRecord(deviceId, PhRecord(generateRandomDouble(), Instant.now()))
        repo.savePhRecord(deviceId, PhRecord(generateRandomDouble(), Instant.now()))
        val phs = repo.getPhRecords(deviceId)
        assertTrue("Temperature found", phs.size == 3)
    }

    @Test
    fun `add 2 temperature records to a device and 1 to another`() {
        val testDBConfig = TSDBConfig().tsdb2Properties()
        val repo = TSDBRepository(testDBConfig)
        val deviceId = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120002")
        val deviceId2 = DeviceId("80acf16c-d3bb-11ed-afa1-0242ac120003")
        repo.savePhRecord(deviceId, PhRecord(generateRandomDouble(), Instant.now()))
        repo.savePhRecord(deviceId, PhRecord(generateRandomDouble(), Instant.now()))
        repo.savePhRecord(deviceId2, PhRecord(generateRandomDouble(), Instant.now()))
        val phs = repo.getPhRecords(deviceId)
        val phs2 = repo.getPhRecords(deviceId2)
        assertTrue("Temperature found", phs.size == 2)
        assertTrue("Temperature found", phs2.size == 1)
    }




}
fun generateRandomDouble(): Double {
    val random = Random()
    return random.nextDouble(-10.0, 10.0)
}