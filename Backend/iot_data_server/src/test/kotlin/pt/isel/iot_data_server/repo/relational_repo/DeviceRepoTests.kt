package pt.isel.iot_data_server.repo.relational_repo

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.iot_data_server.service.user.Role
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback

class DeviceRepoTests {
    private val role = Role.USER
    @Test
    fun `Create Device`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val deviceDataRepository = transaction.deviceRepo
                val userRepo = transaction.userRepo

                val user = createUser(userRepo, "some_email_1@gmail.com")
                val alertEmail = "some_alert_email_1@gmail.com"
                val device = createDevice(deviceDataRepository, user.id, alertEmail)

                val foundDevices = deviceDataRepository.getAllDevices()
                Assertions.assertEquals(device, foundDevices[0])
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
                Assertions.assertEquals(2, foundDevices.size)

                Assertions.assertEquals(device1, foundDevices[0])
                Assertions.assertEquals(device2, foundDevices[1])
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
                Assertions.assertEquals(2, foundDevices.size)

                Assertions.assertEquals(device1, foundDevices[0])
                Assertions.assertEquals(device2, foundDevices[1])
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
                Assertions.assertEquals(3, devices.size)
                Assertions.assertTrue(devices.contains(device1))
                Assertions.assertTrue(devices.contains(device2))
                Assertions.assertTrue(devices.contains(device3))
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
                Assertions.assertEquals(2, devices.size)
                Assertions.assertTrue(devices.contains(device4))
                Assertions.assertTrue(devices.contains(device5))
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

                val devices = devicesRepo.getAllDevicesByUserId(user2.id, 1, 3)
                Assertions.assertEquals(3, devices.size)
                Assertions.assertTrue(devices.contains(device4))
                Assertions.assertTrue(devices.contains(device5))
                Assertions.assertTrue(devices.contains(device6))
            }
        }
    }
}