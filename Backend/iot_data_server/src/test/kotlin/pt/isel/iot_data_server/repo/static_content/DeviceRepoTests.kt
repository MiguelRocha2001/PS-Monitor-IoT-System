package pt.isel.iot_data_server.repo.static_content

import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.service.user.Role
import pt.isel.iot_data_server.utils.generateRandomEmail
import pt.isel.iot_data_server.utils.generateRandomName
import pt.isel.iot_data_server.utils.generateRandomPassword
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback
import kotlin.random.Random

class DeviceRepoTests {
    private val role = Role.USER
    @Test
    fun `add device`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val deviceDataRepository = transaction.deviceRepo
                val userRepo = transaction.userRepo

                val userId = "some_id"

                val device = Device(Random.nextInt().toString(), "exampleEmail@pront.com")
                val user = User(
                    userId,
                    UserInfo(
                        generateRandomEmail(),
                        role
                    )
                )
                userRepo.createUser(user)
                deviceDataRepository.createDevice(userId, device)
                val foundDevices = deviceDataRepository.getAllDevices()
                val foundDevice = foundDevices.any { it.deviceId == device.deviceId }
                assertTrue("Device found", foundDevice)
            }
        }
    }

    @Test
    fun `add 3 devices and get the list`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val devicesRepo = transaction.deviceRepo
                val userRepo = transaction.userRepo

                var devices = devicesRepo.getAllDevices()
                assertTrue("Device found", devices.isEmpty())

                val userId = "some_id"
                val user = User(
                    userId,
                    UserInfo(
                        generateRandomEmail(),
                        role
                    )
                )
                userRepo.createUser(user)

                val device1 = Device(Random.nextInt().toString(), generateRandomEmail())
                val device2 = Device(Random.nextInt().toString(), generateRandomEmail())
                val device3 = Device(Random.nextInt().toString(), generateRandomEmail())

                devicesRepo.createDevice(userId, device1)
                devicesRepo.createDevice(userId, device2)
                devicesRepo.createDevice(userId, device3)

                devices = devicesRepo.getAllDevices()
                assertTrue("Device found", devices.size == 3)
            }
        }
    }

    @Test
    fun `get empty list of devices`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val devicesRepo = transaction.deviceRepo

                val devices = devicesRepo.getAllDevices()

                assertTrue("Device found", devices.isEmpty())
            }
        }
    }
}