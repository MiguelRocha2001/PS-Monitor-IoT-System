import org.eclipse.paho.client.mqttv3.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.Mqtt3ClientBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.domain.SEED
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.repository.tsdb.TSDBConfig
import pt.isel.iot_data_server.repository.tsdb.TSDBRepository
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.email.EmailManager
import pt.isel.iot_data_server.service.sensor_data.SensorDataService
import pt.isel.iot_data_server.utils.generateRandomDouble
import pt.isel.iot_data_server.utils.getRandomInstantWithinLastWeek
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback


class SensorDataServiceTest {

    private lateinit var mqttClient: MqttClient

    @BeforeEach
    fun setup() {
        // Mock Mqtt3Client instance
        mqttClient = Mockito.mock(MqttClient::class.java)
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
            val deviceId = DeviceId("deviceId")
            val phRecord = PhRecord(generateRandomDouble(), getRandomInstantWithinLastWeek())
            val phRecord2 = PhRecord(generateRandomDouble(), getRandomInstantWithinLastWeek())
            val phRecord3 = PhRecord(generateRandomDouble(), getRandomInstantWithinLastWeek())
            sensorDataService.savePhRecord(deviceId, phRecord)

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
        // Mock dependencies
        val tsdbRepository = Mockito.mock(TSDBRepository::class.java)
        val emailSenderService = Mockito.mock(EmailManager::class.java)
        val deviceService = Mockito.mock(DeviceService::class.java)

        // Create SensorDataService instance
        val sensorDataService = SensorDataService(emailSenderService, tsdbRepository, deviceService, mqttClient)

        // Invoke savePhRecord with invalid pH value
        val deviceId = DeviceId("deviceId")
        val phRecord = PhRecord(15.0, getRandomInstantWithinLastWeek())

        // Assert that an exception is thrown with the correct error message

    }
}
