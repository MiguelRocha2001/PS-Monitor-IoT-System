package pt.isel.iot_data_server.service

import org.eclipse.paho.client.mqttv3.MqttClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import pt.isel.iot_data_server.configuration.TSDBBuilder
import pt.isel.iot_data_server.domain.SensorInfo
import pt.isel.iot_data_server.repo.time_series_repo.deleteAllSensorMeasurements
import pt.isel.iot_data_server.repo.time_series_repo.insertDataInInfluxDB
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.repository.tsdb.SensorDataRepo
import pt.isel.iot_data_server.service.email.EmailManager
import pt.isel.iot_data_server.service.sensor_data.SensorDataService
import pt.isel.iot_data_server.utils.generateRandomEmail
import pt.isel.iot_data_server.utils.generateRandomPh
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback
import java.time.Instant

private const val BUCKET_FOR_TESTS = "test" //Its is mandatory to have a bucket named "test" registered in InfluxDB
class SensorDataServiceTest {

    private lateinit var mqttClient: MqttClient
    private val tsdbBuilder: TSDBBuilder = TSDBBuilder(BUCKET_FOR_TESTS)
    private val sensorDataRepo: SensorDataRepo = SensorDataRepo(
        tsdbBuilder.getClient(),
        tsdbBuilder.getBucket()
    )

    class MySensorInfo : SensorInfo {
        override fun getUpperSensorThreshold(sensorName: String): Double? {
            // Implement your logic here to retrieve the sensor threshold based on the sensor name
            // Return the threshold value as a Double or null if it is not found
            return when (sensorName) {
                "temperature" -> 25.0
                "ph initial" -> 6.0
                "ph final" -> 6.0
                else -> null
            }
        }

        override fun getSensorLowerThreshold(sensorName: String): Double? {
            // Implement your logic here to retrieve the sensor threshold based on the sensor name
            // Return the threshold value as a Double or null if it is not found
            return when (sensorName) {
                "temperature" -> 15.0
                "ph initial" -> 5.0
                "ph final" -> 5.0
                else -> null
            }
        }
    }

    @BeforeEach
    fun setup() {
        // Mock Mqtt3Client instance
        mqttClient = Mockito.mock(MqttClient::class.java)
        deleteAllSensorMeasurements(tsdbBuilder, "ph")
        deleteAllSensorMeasurements(tsdbBuilder, "humidity")
        deleteAllSensorMeasurements(tsdbBuilder, "temperature")

        //  deleteAllTemperatureMeasurements(tsdbBuilder)
    }

    @Test
    fun `Get valid device sensor records` () {
        testWithTransactionManagerAndRollback { tra: TransactionManager ->
            val emailSenderService = EmailManager()
            val (deviceService, userService) = getNewDeviceAndUserService(tra)
            val userId = createRandomUser(userService)

            val sensorInfo = MySensorInfo()
            // Create service instance
            val sensorDataService =
                SensorDataService(sensorDataRepo, deviceService)

            val deviceId = (deviceService.createDevice(userId, generateRandomEmail()) as Either.Right).value

            val value = generateRandomPh()
            val instant = Instant.now()
            insertDataInInfluxDB(tsdbBuilder.getClient(), deviceId, "ph", value, instant)

            val res2 = sensorDataService.getSensorRecords(deviceId, "ph")
            assert(res2 is Either.Right)
            val records = (res2 as Either.Right).value
            assertEquals(1, records.size)
        }
    }

    @Test
    fun `Get invalid device sensor records` () {
        testWithTransactionManagerAndRollback { tra: TransactionManager ->
            val emailSenderService = EmailManager()
            val (deviceService, userService) = getNewDeviceAndUserService(tra)
            createRandomUser(userService)

            val sensorInfo = MySensorInfo()
            // Create service instance
            val sensorDataService =
                SensorDataService(sensorDataRepo, deviceService)

            val res2 = sensorDataService.getSensorRecords("invalid", "temperature")
            assert(res2 is Either.Left)
        }
    }

    @Test
    fun `Get valid device sensor records as owner` () {
        testWithTransactionManagerAndRollback { tra: TransactionManager ->
            val emailSenderService = EmailManager()
            val (deviceService, userService) = getNewDeviceAndUserService(tra)
            val userId = createRandomUser(userService)

            val sensorInfo = MySensorInfo()
            // Create service instance
            val sensorDataService =
                SensorDataService(sensorDataRepo, deviceService)

            val email = generateRandomEmail()
            val res = deviceService.createDevice(userId, email)
            val deviceId = (res as Either.Right).value

            insertDataInInfluxDB(tsdbBuilder.getClient(), deviceId, "temperature", 20.0, Instant.now())

            val res2 = sensorDataService.getSensorRecordsIfIsOwner(deviceId, userId, "temperature")
            assert(res2 is Either.Right)
            val records = (res2 as Either.Right).value
            assert(records.isNotEmpty())
        }
    }

    @Test
    fun `Get valid device sensor records not as owner` () {
        testWithTransactionManagerAndRollback { tra: TransactionManager ->
            val emailSenderService = EmailManager()
            val (deviceService, userService) = getNewDeviceAndUserService(tra)
            val userId = createRandomUser(userService)

            val sensorInfo = MySensorInfo()
            // Create service instance
            val sensorDataService =
                SensorDataService(sensorDataRepo, deviceService)

            val email = generateRandomEmail()
            val res = deviceService.createDevice(userId, email)
            val deviceId = (res as Either.Right).value

            val res2 = sensorDataService.getSensorRecordsIfIsOwner(deviceId, "invalid-user", "temperature")
            assert(res2 is Either.Left)
        }
    }

    @Test
    fun `Get available sensors types` () {
        testWithTransactionManagerAndRollback { tra: TransactionManager ->
            val emailSenderService = EmailManager()
            val (deviceService, userService) = getNewDeviceAndUserService(tra)
            val userId = createRandomUser(userService)

            val sensorInfo = MySensorInfo()
            // Create service instance
            val sensorDataService =
                SensorDataService(sensorDataRepo, deviceService)

            val email = generateRandomEmail()
            val res = deviceService.createDevice(userId, email)
            val deviceId = (res as Either.Right).value

            val types = sensorDataService.getAvailableSensors(deviceId)
            types as Either.Right
            assertEquals(0, types.value.size)
        }
    }

    // This tests doesnt make sense since the SensorDataService should not be responsible for inserting the records
    /*
    @Test
    fun testSaveASensorNamedPhRecordWithValidPhValue() {
        testWithTransactionManagerAndRollback { tra: TransactionManager ->
            val emailSenderService = EmailManager()
            val (deviceService, userService) = getNewDeviceAndUserService(tra)
            val userId = createRandomUser(userService)

            val sensorInfo = MySensorInfo()
            // Create service instance
            val sensorDataService =
                SensorDataService(emailSenderService, sensorDataRepo, deviceService, sensorInfo, mqttClient)

            // Invoke savePhRecord with valid pH value
            val email = generateRandomEmail()
            deviceService.addDevice(userId, email)
            val deviceId = deviceService.getDevicesByOwnerEmail(email).first().deviceId
            val phType = "ph initial"
            val phRecord = SensorRecord(phType, generateRandomPh(), getRandomInstantWithinLastWeek())
            val phRecord2 = SensorRecord(phType, generateRandomPh(), getRandomInstantWithinLastWeek())
            val phRecord3 = SensorRecord(phType, generateRandomPh(), getRandomInstantWithinLastWeek())
            sensorDataService.saveSensorRecord(deviceId, phRecord)
            sensorDataService.saveSensorRecord(deviceId, phRecord2)
            sensorDataService.saveSensorRecord(deviceId, phRecord3)

            val result = sensorDataService.getSensorRecords(deviceId, phType)
            assert(result is Either.Right)
            val phRecords = (result as Either.Right).value
            assert(phRecords.size == 3)
            assert(phRecords.contains(phRecord))
            assert(phRecords.contains(phRecord2))
            assert(phRecords.contains(phRecord3))
        }
    }


    @Test
    fun testSaveASensorNamedPhRecordWithInvalidDeviceId() {
        testWithTransactionManagerAndRollback { tra: TransactionManager ->
            val emailSenderService = EmailManager()

            val (deviceService, userService) = getNewDeviceAndUserService(tra)
            val userId = createRandomUser(userService)

            // Create service instance
            val sensorInfo = MySensorInfo()
            // Create service instance
            val sensorDataService =
                SensorDataService(emailSenderService, sensorDataRepo, deviceService, sensorInfo, mqttClient)

            // Invoke savePhRecord with invalid pH value
            val deviceId = "invalid"
            val phRecord = SensorRecord("ph initial", generateRandomPh(), getRandomInstantWithinLastWeek())

            assertThrows<IllegalArgumentException> { sensorDataService.saveSensorRecord(deviceId, phRecord) }
        }
    }
     */

    //TODO ADICIONAR MAIS TESTES
}





