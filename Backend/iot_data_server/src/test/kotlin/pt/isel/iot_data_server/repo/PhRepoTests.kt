package pt.isel.iot_data_server.repo

import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback
import java.sql.Timestamp
import java.time.Instant
import java.util.*

class PhRepoTests {
/*
        @Test
        fun `add ph record and get it`() {
            testWithTransactionManagerAndRollback { transactionManager ->
                transactionManager.run { transaction ->
                    val phRepo = transaction.repository
                    val timestampExample = Instant.now()
                    val ph = PhRecord(1.0, timestampExample)
                    val deviceId = DeviceId("4521087288")
                    phRepo.savePhRecord(deviceId, ph)
                    val foundPh = phRepo.getPhRecords(deviceId)
                    assertTrue("Ph found", foundPh != null)

                }
            }
        }

        @Test
        fun `add 3 ph records and get the list`() {
            testWithTransactionManagerAndRollback { transactionManager ->
                transactionManager.run { transaction ->
                    val phRepo = transaction.repository
                    val timestampExample = Instant.now()
                    val ph1 = PhRecord(1.0, timestampExample)
                    val ph2 = PhRecord(2.0, timestampExample)
                    val ph3 = PhRecord(3.0, timestampExample)
                    val deviceId = DeviceId(UUID.randomUUID())
                    phRepo.savePhRecord(deviceId, ph1)
                    phRepo.savePhRecord(deviceId, ph2)
                    phRepo.savePhRecord(deviceId, ph3)
                    val phs = phRepo.getPhRecords(deviceId)
                    assertTrue("Ph found", phs.size == 3)
                }
            }
        }
    */
}