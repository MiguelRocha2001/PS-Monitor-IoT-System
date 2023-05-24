package pt.isel.iot_data_server.repo.time_series

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.configuration.TSDBBuilder
import pt.isel.iot_data_server.domain.SensorRecord
import pt.isel.iot_data_server.repository.tsdb.SensorDataRepo
import pt.isel.iot_data_server.utils.generateRandomPh
import pt.isel.iot_data_server.utils.generateRandomTemperature
import java.time.Instant

class TsdbRepoTests {
    private val tsdbBuilder: TSDBBuilder = TSDBBuilder("test")
    private val repo: SensorDataRepo = SensorDataRepo(
        tsdbBuilder.getClient(),
        tsdbBuilder.getBucket()
    )

    @AfterEach
    fun deleteAll() {
        deleteAllSensorMeasurements(tsdbBuilder,"temperature")
    }

    @Test
    fun `add a sensor record and get it`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val temperaturesBefore = repo.getSensorRecords(deviceId,"temperature")
        assertTrue("Ph found", temperaturesBefore.isEmpty())
        val sensorRecord = SensorRecord("temperature",generateRandomTemperature(), Instant.now())
        repo.saveSensorRecord(deviceId, sensorRecord)

        val temperatures = repo.getSensorRecords(deviceId,"temperature")
        assertTrue("Temperature found", temperatures.size == 1)
    }

    @Test
    fun `add 3 sensor records and get the list`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val temperaturesBefore = repo.getSensorRecords(deviceId,"temperature")
        assertTrue("Temperatures before", temperaturesBefore.isEmpty())


        repo.saveSensorRecord(deviceId, SensorRecord("temperature",generateRandomTemperature(), Instant.now()))
        repo.saveSensorRecord(deviceId, SensorRecord("temperature",generateRandomTemperature(), Instant.now()))
        repo.saveSensorRecord(deviceId, SensorRecord("temperature",generateRandomTemperature(), Instant.now()))


        val temperatures = repo.getSensorRecords(deviceId,"temperature")
        assertTrue("Temperatures found", temperatures.size == 3)
    }
    @Test
    fun `add 2 sensor records to a device and 1 to another`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val deviceId2 = "80acf16c-d3bb-11ed-afa1-0242ac120003"

        repo.saveSensorRecord(deviceId, SensorRecord("temperature",generateRandomTemperature(), Instant.now()))
        repo.saveSensorRecord(deviceId, SensorRecord("temperature",generateRandomTemperature(), Instant.now()))
        repo.saveSensorRecord(deviceId2, SensorRecord("temperature",generateRandomTemperature(), Instant.now()))

        val phs = repo.getSensorRecords(deviceId,"temperature")
        val phs2 = repo.getSensorRecords(deviceId2,"temperature")

        assertTrue("temperature found", phs.size == 2)
        assertTrue("temperature found", phs2.size == 1)

    }

    @Test
    fun `add 2 temperature records to a device and 1 to another`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val deviceId2 = "80acf16c-d3bb-11ed-afa1-0242ac120003"

        repo.saveSensorRecord(deviceId, SensorRecord("temperature",generateRandomTemperature(), Instant.now()))
        repo.saveSensorRecord(deviceId, SensorRecord("temperature",generateRandomTemperature(), Instant.now()))
        repo.saveSensorRecord(deviceId2, SensorRecord("temperature",generateRandomTemperature(), Instant.now()))


        val phs = repo.getSensorRecords(deviceId,"temperature")
        val phs2 = repo.getSensorRecords(deviceId2,"temperature")

        assertTrue("Temperature found", phs.size == 2)
        assertTrue("Temperature found", phs2.size == 1)

    }

    @Test
    fun `add 1 temperature record and 1 ph record to two devices`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val deviceId2 = "80acf16c-d3bb-11ed-afa1-0242ac120003"

        repo.saveSensorRecord(deviceId,SensorRecord("ph initial",generateRandomPh(), Instant.now()))
        repo.saveSensorRecord(deviceId2,SensorRecord("ph initial",generateRandomPh(), Instant.now()))
        repo.saveSensorRecord(deviceId,SensorRecord("temperature",generateRandomTemperature(), Instant.now()))
        repo.saveSensorRecord(deviceId2,SensorRecord("temperature",generateRandomTemperature(), Instant.now()))

        val phs1 = repo.getSensorRecords(deviceId,"ph initial")
        val temperatures1 = repo.getSensorRecords(deviceId,"temperature")
        val phs2 = repo.getSensorRecords(deviceId2,"ph initial")
        val temperatures2 = repo.getSensorRecords(deviceId2,"temperature")
        assertTrue("Ph found", phs1.size == 1)
        assertTrue( "Temperature found", temperatures1.size == 1)
        assertTrue("Ph found", phs2.size == 1)
        assertTrue("Temperature found", temperatures2.size == 1)
    }
}