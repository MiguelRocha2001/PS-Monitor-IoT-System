package pt.isel.iot_data_server.repo.time_series_repo

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.configuration.TSDBBuilder
import pt.isel.iot_data_server.domain.SensorRecord
import pt.isel.iot_data_server.repository.tsdb.SensorDataRepo
import pt.isel.iot_data_server.utils.generateRandomHumidity
import pt.isel.iot_data_server.utils.generateRandomPh
import pt.isel.iot_data_server.utils.generateRandomTemperature
import java.time.Instant

class TsdbRepoTests {
    private val tsdbBuilder: TSDBBuilder = TSDBBuilder("test")
    private val repo: SensorDataRepo = SensorDataRepo(
        tsdbBuilder.getClient(),
        tsdbBuilder.getBucket()
    )

    @BeforeEach
    fun deleteAll() {
        deleteAllSensorMeasurements(tsdbBuilder,"ph initial")
        deleteAllSensorMeasurements(tsdbBuilder,"temperature")
    }

    @Test
    fun `add a sensor record and get it`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val temperaturesBefore = repo.getSensorRecords(deviceId,"temperature")
        assertTrue("Temperature records should not exist", temperaturesBefore.isEmpty())
        val sensorRecord = SensorRecord("temperature", generateRandomTemperature(), Instant.now())
        repo.saveSensorRecord(deviceId, sensorRecord)

        val temperatures = repo.getSensorRecords(deviceId,"temperature")
        assertTrue("Temperature records size is not 1", temperatures.size == 1)
    }

    @Test
    fun `add 3 sensor records and get the list`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val temperaturesBefore = repo.getSensorRecords(deviceId,"temperature")
        assertTrue("Temperatures before", temperaturesBefore.isEmpty())


        repo.saveSensorRecord(deviceId, SensorRecord("temperature", generateRandomTemperature(), Instant.now()))
        repo.saveSensorRecord(deviceId, SensorRecord("temperature", generateRandomTemperature(), Instant.now()))
        repo.saveSensorRecord(deviceId, SensorRecord("temperature", generateRandomTemperature(), Instant.now()))


        val temperatures = repo.getSensorRecords(deviceId,"temperature")
        assertTrue("Temperature records size is not 3", temperatures.size == 3)
    }
    @Test
    fun `add 2 sensor records to a device and 1 to another`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val deviceId2 = "80acf16c-d3bb-11ed-afa1-0242ac120003"

        repo.saveSensorRecord(deviceId, SensorRecord("temperature", generateRandomTemperature(), Instant.now()))
        repo.saveSensorRecord(deviceId, SensorRecord("temperature", generateRandomTemperature(), Instant.now()))
        repo.saveSensorRecord(deviceId2, SensorRecord("temperature", generateRandomTemperature(), Instant.now()))

        val phs = repo.getSensorRecords(deviceId,"temperature")
        val phs2 = repo.getSensorRecords(deviceId2,"temperature")

        assertTrue("Size is not 2", phs.size == 2)
        assertTrue("Size is not 1", phs2.size == 1)
    }

    @Test
    fun `add 1 temperature record and 1 ph record to two devices`() {
        val deviceId = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        val deviceId2 = "80acf16c-d3bb-11ed-afa1-0242ac120003"

        repo.saveSensorRecord(deviceId, SensorRecord("ph initial", generateRandomPh(), Instant.now()))
        repo.saveSensorRecord(deviceId2, SensorRecord("ph initial", generateRandomPh(), Instant.now()))
        repo.saveSensorRecord(deviceId, SensorRecord("temperature", generateRandomTemperature(), Instant.now()))
        repo.saveSensorRecord(deviceId2, SensorRecord("temperature", generateRandomTemperature(), Instant.now()))

        val phs1 = repo.getSensorRecords(deviceId,"ph initial")
        val temperatures1 = repo.getSensorRecords(deviceId,"temperature")
        val phs2 = repo.getSensorRecords(deviceId2,"ph initial")
        val temperatures2 = repo.getSensorRecords(deviceId2,"temperature")

        assertTrue("Ph found", phs1.size == 1)
        assertTrue( "Temperature found", temperatures1.size == 1)
        assertTrue("Ph found", phs2.size == 1)
        assertTrue("Temperature found", temperatures2.size == 1)
    }

    @Test
    fun `Get all Sensor Types`() {
        val deviceId1 = "80acf16c-d3bb-11ed-afa1-0242ac120002"
        repo.saveSensorRecord(deviceId1, SensorRecord("ph initial", generateRandomPh(), Instant.now()))
        repo.saveSensorRecord(deviceId1, SensorRecord("temperature", generateRandomTemperature(), Instant.now()))

        val deviceId2 = "80acf16c-d3bb-11ed-afa1-0242ac120003"
        repo.saveSensorRecord(deviceId2, SensorRecord("ph final", generateRandomPh(), Instant.now()))
        repo.saveSensorRecord(deviceId2, SensorRecord("humidity", generateRandomHumidity(), Instant.now()))
        repo.saveSensorRecord(deviceId2, SensorRecord("temperature", generateRandomTemperature(), Instant.now()))

        val sensorTypes1 = repo.getAvailableSensorTypes(deviceId1)
        val sensorTypes2 = repo.getAvailableSensorTypes(deviceId2)

        assertTrue("Sensor types size is not 2", sensorTypes1.size == 2)
        assertTrue("Sensor types size is not 3", sensorTypes2.size == 3)

        assert(sensorTypes1.find { it == "ph initial" } != null)
        assert(sensorTypes1.find { it == "temperature" } != null)

        assert(sensorTypes2.find { it == "ph final" } != null)
        assert(sensorTypes2.find { it == "humidity" } != null)
        assert(sensorTypes2.find { it == "temperature" } != null)
    }
}