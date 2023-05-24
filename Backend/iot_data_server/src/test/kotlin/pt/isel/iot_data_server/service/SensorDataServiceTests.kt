package pt.isel.iot_data_server.service

import org.eclipse.paho.client.mqttv3.MqttClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import pt.isel.iot_data_server.configuration.TSDBBuilder
import pt.isel.iot_data_server.domain.SensorInfo
import pt.isel.iot_data_server.domain.SensorRecord
import pt.isel.iot_data_server.repo.time_series.deleteAllPhMeasurements
import pt.isel.iot_data_server.repo.time_series.deleteAllSensorMeasurements
import pt.isel.iot_data_server.repo.time_series.deleteAllTemperatureMeasurements
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.repository.tsdb.SensorDataRepo
import pt.isel.iot_data_server.service.email.EmailManager
import pt.isel.iot_data_server.service.sensor_data.SensorDataService
import pt.isel.iot_data_server.utils.*

private const val BUCKET_FOR_TESTS = "test" //Its is mandatory to have a bucket named "test" registered in InfluxDB
class SensorDataServiceTest {

    private lateinit var mqttClient: MqttClient
    private val tsdbBuilder: TSDBBuilder = TSDBBuilder(BUCKET_FOR_TESTS)
    private val sensorDataRepo: SensorDataRepo = SensorDataRepo(
        tsdbBuilder.getClient(),
        tsdbBuilder.getBucket()
    )

    class MySensorInfo : SensorInfo {
        override fun getSensorThreshold(sensorName: String): Double? {
            // Implement your logic here to retrieve the sensor threshold based on the sensor name
            // Return the threshold value as a Double or null if it is not found
            return when (sensorName) {
                "temperature" -> 25.0
                "ph initial" -> 6.0
                "ph final" -> 6.0
                else -> null
            }
        }
    }

    @BeforeEach
    fun setup() {
        // Mock Mqtt3Client instance
        mqttClient = Mockito.mock(MqttClient::class.java)
        deleteAllSensorMeasurements(tsdbBuilder, "initial Ph")

        //  deleteAllTemperatureMeasurements(tsdbBuilder)
    }

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
            val email = generateRandomEmail()
            deviceService.addDevice(userId, email)
            val deviceId = "invalid"
            val phRecord = SensorRecord("ph initial", generateRandomPh(), getRandomInstantWithinLastWeek())

            try {
                // Code that may throw an exception
                sensorDataService.saveSensorRecord(deviceId, phRecord)
            } catch (e: IllegalArgumentException) {
                // Access the exception message
                val errorMessage = e.message

                // Assert that the exception message is as expected
                assertEquals("Invalid device id", errorMessage)
            }
        }
    }

    //TODO ADICIONAR MAIS TESTES
}





