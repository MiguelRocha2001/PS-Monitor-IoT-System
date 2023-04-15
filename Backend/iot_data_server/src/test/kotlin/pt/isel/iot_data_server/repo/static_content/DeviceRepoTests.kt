package pt.isel.iot_data_server.repo.static_content

import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.utils.generateRandomEmail
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback
import kotlin.random.Random

class DeviceRepoTests {

    @Test
    fun `add device`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run {transaction ->
                val devicesRepo = transaction.repository
                val device = Device(DeviceId(Random.nextInt().toString()), "exampleEmail@pront.com")
                devicesRepo.addDevice(device)
                val foundDevice = devicesRepo.getAllDevices().any { it.deviceId.id == device.deviceId.id }
                assertTrue("Device found", foundDevice)
            }
        }
    }

    @Test
    fun `add 3 devices and get the list`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run {transaction ->
                val devicesRepo = transaction.repository
                var devices = devicesRepo.getAllDevices()
                assertTrue("Device found", devices.isEmpty())

                val device1 = Device(DeviceId(Random.nextInt().toString()), generateRandomEmail())
                val device2 = Device(DeviceId(Random.nextInt().toString()), generateRandomEmail())
                val device3 = Device(DeviceId(Random.nextInt().toString()), generateRandomEmail())
                devicesRepo.addDevice(device1)
                devicesRepo.addDevice(device2)
                devicesRepo.addDevice(device3)
                devices = devicesRepo.getAllDevices()
                assertTrue("Device found", devices.size == 3)
            }
        }
    }

    @Test
    fun `get empty list of devices`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run {transaction ->
                val devicesRepo = transaction.repository
                val devices = devicesRepo.getAllDevices()
                assertTrue("Device found", devices.isEmpty())
            }
        }
    }

}