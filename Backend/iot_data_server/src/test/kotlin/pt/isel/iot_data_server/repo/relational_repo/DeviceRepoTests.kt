package pt.isel.iot_data_server.repo.relational_repo

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pt.isel.iot_data_server.domain.DeviceWakeUpLog
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndDontRollback
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback
import java.time.Instant

class DeviceRepoTests {
    @BeforeEach
    fun preparation() {
        testWithTransactionManagerAndDontRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo
                val devicesRepo = transaction.deviceRepo
                // TODO: maybe delete all device logs
                devicesRepo.deleteAllDevices()
                usersRepo.deleteAllPasswords()
                usersRepo.deleteAllTokens()
                usersRepo.deleteAllUsers()
            }
        }
    }
    @Test
    fun `Create Device`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val deviceDataRepository = transaction.deviceRepo
                val userRepo = transaction.userRepo

                val user = createUser(userRepo, "some_email_1@gmail.com")
                val alertEmail = "some_alert_email_1@gmail.com"
                val device = createDevice(deviceDataRepository, user.id, alertEmail)

                assertEquals(device, deviceDataRepository.getDeviceById(device.deviceId))
            }
        }
    }

    @Test
    fun `User creates two devices with the same alert email`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val deviceDataRepository = transaction.deviceRepo
                val userRepo = transaction.userRepo

                checkDeviceCountIsZero(deviceDataRepository)

                val user = createUser(userRepo, "some_email_1@gmail.com")
                val alertEmail = "some_alert_email_1@gmail.com"
                val device1 = createDevice(deviceDataRepository, user.id, alertEmail)
                val device2 = createDevice(deviceDataRepository, user.id, alertEmail)

                val foundDevices = deviceDataRepository.getAllDevices()
                assertEquals(2, foundDevices.size)

                assertEquals(device1, foundDevices[0])
                assertEquals(device2, foundDevices[1])
            }
        }
    }

    @Test
    fun `Two users create a device with the same alert email`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val deviceDataRepository = transaction.deviceRepo
                val userRepo = transaction.userRepo

                checkDeviceCountIsZero(deviceDataRepository)

                val user1 = createUser(userRepo, "some_email_1@gmail.com")
                val user2 = createUser(userRepo, "some_email_2@gmail.com")
                val alertEmail = "some_alert_email_1@gmail.com"
                val device1 = createDevice(deviceDataRepository, user1.id, alertEmail)
                val device2 = createDevice(deviceDataRepository, user2.id, alertEmail)

                val foundDevices = deviceDataRepository.getAllDevices()
                assertEquals(2, foundDevices.size)

                assertEquals(device1, foundDevices[0])
                assertEquals(device2, foundDevices[1])
            }
        }
    }

    @Test
    fun `Add 3 devices and get the list`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val devicesRepo = transaction.deviceRepo
                val userRepo = transaction.userRepo

                checkDeviceCountIsZero(devicesRepo)

                val user = createUser(userRepo, "some_email_1@gmail.com")

                val device1 = createDevice(devicesRepo, user.id, "some_alert_email_1@gmail.com")
                val device2 = createDevice(devicesRepo, user.id, "some_alert_email_2@gmail.com")
                val device3 = createDevice(devicesRepo, user.id, "some_alert_email_3@gmail.com")

                val devices = devicesRepo.getAllDevices()
                assertEquals(3, devices.size)
                assertTrue(devices.contains(device1))
                assertTrue(devices.contains(device2))
                assertTrue(devices.contains(device3))
            }
        }
    }

    @Test
    fun `Device pagination`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val devicesRepo = transaction.deviceRepo
                val userRepo = transaction.userRepo

                checkDeviceCountIsZero(devicesRepo)

                val user = createUser(userRepo, "some_email_1@gmail.com")

                val device1 = createDevice(devicesRepo, user.id, "some_alert_email_1@gmail.com")
                val device2 = createDevice(devicesRepo, user.id, "some_alert_email_2@gmail.com")
                val device3 = createDevice(devicesRepo, user.id, "some_alert_email_3@gmail.com")
                val device4 = createDevice(devicesRepo, user.id, "some_alert_email_4@gmail.com")
                val device5 = createDevice(devicesRepo, user.id, "some_alert_email_5@gmail.com")

                val devices = devicesRepo.getAllDevices(2, 3)
                assertEquals(2, devices.size)
                assertTrue(devices.contains(device4))
                assertTrue(devices.contains(device5))
            }
        }
    }

    @Test
    fun `User devices pagination`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val devicesRepo = transaction.deviceRepo
                val userRepo = transaction.userRepo

                checkDeviceCountIsZero(devicesRepo)

                val user1 = createUser(userRepo, "some_email_1@gmail.com")
                val user2 = createUser(userRepo, "some_email_2@gmail.com")


                val device1 = createDevice(devicesRepo, user1.id, "some_alert_email_1@gmail.com")
                val device2 = createDevice(devicesRepo, user1.id, "some_alert_email_2@gmail.com")
                val device3 = createDevice(devicesRepo, user1.id, "some_alert_email_3@gmail.com")
                val device4 = createDevice(devicesRepo, user2.id, "some_alert_email_4@gmail.com")
                val device5 = createDevice(devicesRepo, user2.id, "some_alert_email_5@gmail.com")
                val device6 = createDevice(devicesRepo, user2.id, "some_alert_email_6@gmail.com")
                val device7 = createDevice(devicesRepo, user2.id, "some_alert_email_7@gmail.com")
                val device8 = createDevice(devicesRepo, user2.id, "some_alert_email_8@gmail.com")

                val devices = devicesRepo.getAllDevicesByUserId(user2.id, 1, 3, null, null)
                assertEquals(3, devices.size)
                assertTrue(devices.contains(device4))
                assertTrue(devices.contains(device5))
                assertTrue(devices.contains(device6))
            }
        }
    }

    @Test
    fun `Delete Device`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val devicesRepo = transaction.deviceRepo
                val userRepo = transaction.userRepo

                val user = createUser(userRepo, "some_email_1@gmail.com")
                val device = createDevice(devicesRepo, user.id, "some_alert_email_8@gmail.com")

                assertEquals(device, devicesRepo.getDeviceById(device.deviceId))

                devicesRepo.deleteDevice(device.deviceId)
                Assertions.assertNull(devicesRepo.getDeviceById(device.deviceId))
            }
        }
    }

    @Test
    fun `Remove All Devices`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val devicesRepo = transaction.deviceRepo
                val userRepo = transaction.userRepo

                val user = createUser(userRepo, "some_email_1@gmail.com")

                checkDeviceCountIsZero(devicesRepo)
                createDevice(devicesRepo, user.id, "some_alert_email_1@gmail.com")
                createDevice(devicesRepo, user.id, "some_alert_email_2@gmail.com")
                createDevice(devicesRepo, user.id, "some_alert_email_3@gmail.com")

                assertEquals(3, devicesRepo.getAllDevices().size)

                devicesRepo.deleteAllDevices()
                assertEquals(0, devicesRepo.getAllDevices().size)
            }
        }
    }

    @Test
    fun `Get devices filtered by alert email`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val devicesRepo = transaction.deviceRepo
                val userRepo = transaction.userRepo

                val user = createUser(userRepo, "some_email_1@gmail.com")

                checkDeviceCountIsZero(devicesRepo)
                createDevice(devicesRepo, user.id, "some_alert_email_1@gmail.com")
                val device1 = createDevice(devicesRepo, user.id, "some_alert_email_2@gmail.com")
                val device2 = createDevice(devicesRepo, user.id, "some_alert_email_2@gmail.com")
                val device3 = createDevice(devicesRepo, user.id, "some_alert_email_2@gmail.com")
                createDevice(devicesRepo, user.id, "some_alert_email_3@gmail.com")
                assertEquals(5, devicesRepo.getAllDevices().size)

                val devices = devicesRepo.getAllDevicesByUserId(user.id, deviceAlertEmail = "some_alert_email_2@gmail.com")
                assertEquals(3, devices.size)
                assertTrue(devices.contains(device1))
                assertTrue(devices.contains(device2))
                assertTrue(devices.contains(device3))
            }
        }
    }

    @Test
    fun `Device Count`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val devicesRepo = transaction.deviceRepo
                val userRepo = transaction.userRepo

                val user1 = createUser(userRepo, "some_email_1@gmail.com")
                val user2 = createUser(userRepo, "some_email_2@gmail.com")

                checkDeviceCountIsZero(devicesRepo)
                createDevice(devicesRepo, user1.id, "some_alert_email_1@gmail.com")
                createDevice(devicesRepo, user2.id, "some_alert_email_2@gmail.com")
                createDevice(devicesRepo, user1.id, "some_alert_email_2@gmail.com")
                createDevice(devicesRepo, user2.id, "some_alert_email_2@gmail.com")
                createDevice(devicesRepo, user1.id, "some_alert_email_3@gmail.com")

                assertEquals(3, devicesRepo.deviceCount(user1.id))
                assertEquals(2, devicesRepo.deviceCount(user2.id))
            }
        }
    }

    @Test
    fun `Create Device log record`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val devicesRepo = transaction.deviceRepo
                val userRepo = transaction.userRepo

                val user = createUser(userRepo, "some_email_1@gmail.com")

                checkDeviceCountIsZero(devicesRepo)
                val device1 = createDevice(devicesRepo, user.id, "some_alert_email_1@gmail.com")
                val device2 = createDevice(devicesRepo, user.id, "some_alert_email_2@gmail.com")

                val log1 = DeviceWakeUpLog(device1.deviceId, Instant.now(), "some_log_record1")
                val log2 = DeviceWakeUpLog(device1.deviceId, Instant.now().plusMillis(1000), "some_log_record2")
                val log3 = DeviceWakeUpLog(device1.deviceId, Instant.now().plusMillis(2000), "some_log_record3")
                devicesRepo.createDeviceWakeUpLogs(device1.deviceId, log1)
                devicesRepo.createDeviceWakeUpLogs(device1.deviceId, log2)
                devicesRepo.createDeviceWakeUpLogs(device1.deviceId, log3)

                val log4 = DeviceWakeUpLog(device2.deviceId, Instant.now().plusMillis(3000), "some_log_record2")
                val log5 = DeviceWakeUpLog(device2.deviceId, Instant.now().plusMillis(4000), "some_log_record6")
                devicesRepo.createDeviceWakeUpLogs(device2.deviceId, log4)
                devicesRepo.createDeviceWakeUpLogs(device2.deviceId, log5)

                val logRecords = devicesRepo.getDeviceWakeUpLogs(device1.deviceId)
                assertEquals(3, logRecords.size)
                assertTrue(logRecords.contains(log1))
                assertTrue(logRecords.contains(log2))
                assertTrue(logRecords.contains(log3))

                val logRecords2 = devicesRepo.getDeviceWakeUpLogs(device2.deviceId)
                assertEquals(2, logRecords2.size)
                assertTrue(logRecords2.contains(log4))
                assertTrue(logRecords2.contains(log5))
            }
        }
    }

    // TODO: add more tests for device log records
}