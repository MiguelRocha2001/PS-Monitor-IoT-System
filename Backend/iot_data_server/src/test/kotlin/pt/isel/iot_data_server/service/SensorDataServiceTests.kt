package pt.isel.iot_data_server.service

import org.eclipse.paho.client.mqttv3.MqttClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import pt.isel.iot_data_server.configuration.TSDBBuilder
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.domain.TemperatureRecord
import pt.isel.iot_data_server.repo.time_series.deleteAllPhMeasurements
import pt.isel.iot_data_server.repo.time_series.deleteAllTemperatureMeasurements
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.repository.tsdb.SensorDataRepo
import pt.isel.iot_data_server.service.email.EmailManager
import pt.isel.iot_data_server.service.sensor_data.PhDataService
import pt.isel.iot_data_server.service.sensor_data.TemperatureDataService
import pt.isel.iot_data_server.utils.*


class SensorDataServiceTest {
    private lateinit var mqttClient: MqttClient
    private val tsdbBuilder: TSDBBuilder = TSDBBuilder("test")
    private val sensorDataRepo: SensorDataRepo = SensorDataRepo(
        tsdbBuilder.getClient(),
        tsdbBuilder.getBucket()
    )

    @BeforeEach
    fun setup() {
        // Mock Mqtt3Client instance
        mqttClient = Mockito.mock(MqttClient::class.java)
        deleteAllPhMeasurements(tsdbBuilder)
        deleteAllTemperatureMeasurements(tsdbBuilder)
    }

    @Test
    fun testSavePhRecordWithValidPhValue() {
        testWithTransactionManagerAndRollback { tra: TransactionManager ->
            val emailSenderService = EmailManager()

            val (deviceService, userService) = getNewDeviceAndUserService(tra)
            val userId = createRandomUser(userService)

            // Create service instance
            val sensorDataService = PhDataService(emailSenderService, sensorDataRepo, deviceService, mqttClient)

            // Invoke savePhRecord with valid pH value
            val email = generateRandomEmail()
            deviceService.addDevice(userId, email)
            val deviceId = deviceService.getDevicesByOwnerEmail(email).first().deviceId
            val phRecord = PhRecord(generateRandomPh(), getRandomInstantWithinLastWeek())
            val phRecord2 = PhRecord(generateRandomPh(), getRandomInstantWithinLastWeek())
            val phRecord3 = PhRecord(generateRandomPh(), getRandomInstantWithinLastWeek())
            sensorDataService.savePhRecord(deviceId, phRecord)
            sensorDataService.savePhRecord(deviceId, phRecord2)
            sensorDataService.savePhRecord(deviceId, phRecord3)

            val result = sensorDataService.getPhRecords(deviceId)
            assert(result is Either.Right)
            val phRecords = (result as Either.Right).value
            assert(phRecords.size == 3)
            assert(phRecords.contains(phRecord))
            assert(phRecords.contains(phRecord2))
            assert(phRecords.contains(phRecord3))
        }
    }

    @Test
    fun testSavePhRecordWithInvalidPhValue() {
        testWithTransactionManagerAndRollback { tra: TransactionManager ->
            val emailSenderService = EmailManager()

            val (deviceService, userService) = getNewDeviceAndUserService(tra)
            val userId = createRandomUser(userService)

            // Create service instance
            val sensorDataService = PhDataService(emailSenderService, sensorDataRepo, deviceService, mqttClient)

            // Invoke savePhRecord with invalid pH value
            val email = generateRandomEmail()
            deviceService.addDevice(userId, email)
            val deviceId = deviceService.getDevicesByOwnerEmail(email).first().deviceId
            val phRecord = PhRecord(-100.0, getRandomInstantWithinLastWeek())

            try {
                // Code that may throw an exception
                sensorDataService.savePhRecord(deviceId, phRecord)
            } catch (e: IllegalArgumentException) {
                // Access the exception message
                val errorMessage = e.message

                // Assert that the exception message is as expected
                assertEquals("Invalid pH value", errorMessage)
            }
        }
    }

    @Test
    fun testSavePhRecordWithInvalidDeviceId() {
        testWithTransactionManagerAndRollback { tra: TransactionManager ->
            val emailSenderService = EmailManager()

            val (deviceService, userService) = getNewDeviceAndUserService(tra)
            val userId = createRandomUser(userService)

            // Create service instance
            val sensorDataService = PhDataService(emailSenderService, sensorDataRepo, deviceService, mqttClient)

            // Invoke savePhRecord with invalid pH value
            val email = generateRandomEmail()
            deviceService.addDevice(userId, email)
            val deviceId = "invalid"
            val phRecord = PhRecord(generateRandomPh(), getRandomInstantWithinLastWeek())

            try {
                // Code that may throw an exception
                sensorDataService.savePhRecord(deviceId, phRecord)
            } catch (e: IllegalArgumentException) {
                // Access the exception message
                val errorMessage = e.message

                // Assert that the exception message is as expected
                assertEquals("Invalid device id", errorMessage)
            }
        }
    }

    @Test
    fun testGetTemperatureRecordWithInvalidDeviceId() {
        testWithTransactionManagerAndRollback { tra: TransactionManager ->
            val emailSenderService = EmailManager()

            val (deviceService, userService) = getNewDeviceAndUserService(tra)
            val userId = createRandomUser(userService)

            // Create service instance
            val sensorDataService = TemperatureDataService(emailSenderService, sensorDataRepo, deviceService, mqttClient)

            // Invoke savePhRecord with invalid pH value
            val email = generateRandomEmail()
            deviceService.addDevice(userId, email)
            val deviceId = "invalid"

            try {
                // Code that may throw an exception
                sensorDataService.getTemperatureRecords(deviceId)
            } catch (e: IllegalArgumentException) {
                // Access the exception message
                val errorMessage = e.message

                // Assert that the exception message is as expected
                assertEquals("Invalid device id", errorMessage)
            }
        }
    }

    @Test
    fun testSaveValidTemperatureRecord() {
        testWithTransactionManagerAndRollback { tra: TransactionManager ->
            val emailSenderService = EmailManager()

            val (deviceService, userService) = getNewDeviceAndUserService(tra)
            val userId = createRandomUser(userService)

            val sensorDataService = TemperatureDataService(emailSenderService, sensorDataRepo, deviceService, mqttClient)

            val email = generateRandomEmail()
            deviceService.addDevice(userId, email)
            val deviceId = deviceService.getDevicesByOwnerEmail(email).first().deviceId
            val temperatureRecord = TemperatureRecord(20.0, getRandomInstantWithinLastWeek())
            val temperatureRecord2 = TemperatureRecord(20.0, getRandomInstantWithinLastWeek())
            val temperatureRecord3 = TemperatureRecord(20.0, getRandomInstantWithinLastWeek())
            sensorDataService.saveTemperatureRecord(deviceId, temperatureRecord)
            sensorDataService.saveTemperatureRecord(deviceId, temperatureRecord2)
            sensorDataService.saveTemperatureRecord(deviceId, temperatureRecord3)

            val result = sensorDataService.getTemperatureRecords(deviceId)
            assert(result is Either.Right)
            val temperatureRecords = (result as Either.Right).value
            assert(temperatureRecords.size == 3)
            assert(temperatureRecords.contains(temperatureRecord))
            assert(temperatureRecords.contains(temperatureRecord2))
            assert(temperatureRecords.contains(temperatureRecord3))

        }
    }


    @Test
    fun testSaveInvalidTemperatureRecord() {
        testWithTransactionManagerAndRollback { tra: TransactionManager ->
            val emailSenderService = EmailManager()

            val (deviceService, userService) = getNewDeviceAndUserService(tra)
            val userId = createRandomUser(userService)

            val sensorDataService = TemperatureDataService(emailSenderService, sensorDataRepo, deviceService, mqttClient)

            val email = generateRandomEmail()
            deviceService.addDevice(userId, email)
            val deviceId = deviceService.getDevicesByOwnerEmail(email).first().deviceId
            val temperatureRecord = TemperatureRecord(-100.0, getRandomInstantWithinLastWeek())

            try {
                // Code that may throw an exception
                sensorDataService.saveTemperatureRecord(deviceId, temperatureRecord)
            } catch (e: IllegalArgumentException) {
                // Access the exception message
                val errorMessage = e.message

                // Assert that the exception message is as expected
                assertEquals("Invalid temperature value", errorMessage)
            }
        }
    }
}
