package pt.isel.iot_data_server.service

import org.eclipse.paho.client.mqttv3.MqttClient
import deleteAllPhMeasurements
import deleteAllTemperatureMeasurements
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.util.Assert
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.domain.SEED
import pt.isel.iot_data_server.domain.TemperatureRecord
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.repository.tsdb.TSDBConfig
import pt.isel.iot_data_server.repository.tsdb.TSDBRepository
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.email.EmailManager
import pt.isel.iot_data_server.service.sensor_data.SensorDataService
import pt.isel.iot_data_server.utils.generateRandomEmail
import pt.isel.iot_data_server.utils.generateRandomPh
import pt.isel.iot_data_server.utils.getRandomInstantWithinLastWeek
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback
import java.lang.AssertionError


class SensorDataServiceTest {

    private lateinit var mqttClient: MqttClient

    @BeforeEach
    fun setup() {
        // Mock Mqtt3Client instance
        mqttClient = Mockito.mock(MqttClient::class.java)
        val testDBConfig = TSDBConfig().tsdb2Properties()
        deleteAllPhMeasurements(testDBConfig)
        deleteAllTemperatureMeasurements(testDBConfig)
    }

    @Test
    fun testSavePhRecordWithValidPhValue() {
        testWithTransactionManagerAndRollback { tra: TransactionManager ->

            val testDBConfig = TSDBConfig().tsdb2Properties()
            val tsdbRepository = TSDBRepository(testDBConfig)
            val emailSenderService = EmailManager()
            val deviceService = DeviceService( tra,SEED.NANOSECOND)

            // Create SensorDataService instance
            val sensorDataService = SensorDataService(emailSenderService, tsdbRepository, deviceService, mqttClient)

            // Invoke savePhRecord with valid pH value
            val email = generateRandomEmail()
            deviceService.addDevice(email)
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

            val testDBConfig = TSDBConfig().tsdb2Properties()
            val tsdbRepository = TSDBRepository(testDBConfig)
            val emailSenderService = EmailManager()
            val deviceService = DeviceService( tra,SEED.NANOSECOND)

            // Create SensorDataService instance
            val sensorDataService = SensorDataService(emailSenderService, tsdbRepository, deviceService, mqttClient)

            // Invoke savePhRecord with invalid pH value
            val email = generateRandomEmail()
            deviceService.addDevice(email)
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

            val testDBConfig = TSDBConfig().tsdb2Properties()
            val tsdbRepository = TSDBRepository(testDBConfig)
            val emailSenderService = EmailManager()
            val deviceService = DeviceService( tra,SEED.NANOSECOND)

            // Create SensorDataService instance
            val sensorDataService = SensorDataService(emailSenderService, tsdbRepository, deviceService, mqttClient)

            // Invoke savePhRecord with invalid pH value
            val email = generateRandomEmail()
            deviceService.addDevice(email)
            val deviceId = DeviceId("invalid")
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
    fun testGetTemperatureRecordWithInvalidDeviceId(){
        testWithTransactionManagerAndRollback { tra: TransactionManager ->

            val testDBConfig = TSDBConfig().tsdb2Properties()
            val tsdbRepository = TSDBRepository(testDBConfig)
            val emailSenderService = EmailManager()
            val deviceService = DeviceService( tra,SEED.NANOSECOND)

            // Create SensorDataService instance
            val sensorDataService = SensorDataService(emailSenderService, tsdbRepository, deviceService, mqttClient)

            // Invoke savePhRecord with invalid pH value
            val email = generateRandomEmail()
            deviceService.addDevice(email)
            val deviceId = DeviceId("invalid")

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
    fun testSaveValidTemperatureRecord(){
        testWithTransactionManagerAndRollback { tra: TransactionManager ->
            val testDBConfig = TSDBConfig().tsdb2Properties()
            val tsdbRepository = TSDBRepository(testDBConfig)
            val emailSenderService = EmailManager()
            val deviceService = DeviceService( tra,SEED.NANOSECOND)

            val sensorDataService = SensorDataService(emailSenderService, tsdbRepository, deviceService, mqttClient)

            val email = generateRandomEmail()
            deviceService.addDevice(email)
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
    fun testSaveInvalidTemperatureRecord(){
        testWithTransactionManagerAndRollback { tra: TransactionManager ->
            val testDBConfig = TSDBConfig().tsdb2Properties()
            val tsdbRepository = TSDBRepository(testDBConfig)
            val emailSenderService = EmailManager()
            val deviceService = DeviceService( tra,SEED.NANOSECOND)

            val sensorDataService = SensorDataService(emailSenderService, tsdbRepository, deviceService, mqttClient)

            val email = generateRandomEmail()
            deviceService.addDevice(email)
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
