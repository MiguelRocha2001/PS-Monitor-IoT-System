package pt.isel.iot_data_server.service

import com.hivemq.embedded.EmbeddedHiveMQ
import com.hivemq.embedded.EmbeddedHiveMQBuilder
import org.eclipse.paho.client.mqttv3.MqttClient
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Primary
import pt.isel.iot_data_server.HiveMQManager
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.domain.TemperatureRecord
import pt.isel.iot_data_server.repository.tsdb.TSDBRepository
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback
import java.time.Instant
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class SensorDataTests {
    companion object {
        private val embeddedHiveMQBuilder: EmbeddedHiveMQBuilder = EmbeddedHiveMQ.builder()
        private val hiveMQ: EmbeddedHiveMQ = embeddedHiveMQBuilder.build()

        val mqttClient = MqttClient("tcp://localhost:1883", MqttClient.generateClientId())
        @JvmStatic
        @BeforeAll
        fun start() {
            try {
                hiveMQ.start().join()
                mqttClient.connect()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        @JvmStatic
        @AfterAll
        fun stop() {
            hiveMQ.stop().join()
            mqttClient.disconnect()
        }
    }

    @Test
    fun addPhDataTest() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val deviceService = DeviceService(transactionManager)
            val repo = TSDBRepository()

            val sensorData = SensorDataService(repo, deviceService, mqttClient)

            val deviceId = DeviceId("some_id")
            val ownerEmail = "some_email"
            val ownerMobile = 934846723L
            val device = Device(deviceId, ownerEmail, ownerMobile)

            deviceService.addDevice(device)

            //get ph records, should be empty
            val phRecordsResult1 = sensorData.getPhRecords(deviceId)
            assert(phRecordsResult1 is Either.Right && phRecordsResult1.value.isEmpty())

            val time = Instant.now()
            val phRecord = PhRecord(7.5, time)
            sensorData.savePhRecord(deviceId, phRecord)

            val phRecordsResult2 = sensorData.getPhRecords(deviceId)
            assert(
                phRecordsResult2 is Either.Right &&
                        phRecordsResult2.value.size == 1 &&
                        phRecordsResult2.value[0].value == 7.5 &&
                        phRecordsResult2.value[0].instant == time
            )
        }
    }

    /* TODO: Uncomment and fix this test
    @Test
    fun addTemperatureDataTest() {
        val repo = TSDBRepository()
        val sensorData = SensorDataService(repo)
        val id = UUID.randomUUID()
        val deviceId = DeviceId(id)

        //get temperature records, should be empty
        val emptyTemperatureRecords = sensorData.getTemperatureRecords(deviceId)
        assert(emptyTemperatureRecords.size == 0)

        val time = Instant.now()
        val temperatureRecord = TemperatureRecord(7.5,time)
        sensorData.saveTemperatureRecord(deviceId, temperatureRecord)

        val temperatureRecords = sensorData.getTemperatureRecords(deviceId)
        assert(temperatureRecords.size == 1)
        assert(temperatureRecords[0].value == 7.5)
        assert(temperatureRecords[0].instant.equals(time))
    }


    @Test
    fun concurrentAddPhDataTest() {
        val repo = TSDBRepository()
        val sensorData = SensorDataService(repo)

        val numThreads = 10
        val numRecordsPerThread = 100

        // Spawn multiple threads to insert ph records concurrently
        val threads = mutableListOf<Thread>()
        repeat(numThreads) { threadId ->
            threads.add(Thread {
                val deviceId = DeviceId(UUID.randomUUID())
                repeat(numRecordsPerThread) { i ->
                    val time = Instant.now()
                    val phRecord = PhRecord(randomPh(), time)
                    sensorData.savePhRecord(deviceId, phRecord)
                }
            })
        }

        // Start all threads
        threads.forEach { it.start() }

        // Wait for all threads to complete
        threads.forEach { it.join() }

        // Verify that all records were inserted
        val allPhRecords = sensorData.getAllPhRecords()
        assert(allPhRecords.size == numThreads * numRecordsPerThread)
    }

    private fun randomPh(): Double {
        return ThreadLocalRandom.current().nextDouble(0.0, 14.0)
    }

     */
}
