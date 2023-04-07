package pt.isel.iot_data_server.repo

import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback

class DeviceRepoTests {
//FIXME URGENTE METER BASE DE DADOS DE TESTS
    @Test
    fun `add device`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run {transaction ->
                val deviceId = DeviceId("someId")
                val ownerName = "ownerName"
                val ownerMobile = 912345678L

                val devicesRepo = transaction.repository
                val device = Device(deviceId, ownerName, ownerMobile)
                devicesRepo.addDevice(device)
                val foundDevice = devicesRepo.getAllDevices().firstOrNull { it.deviceId == device.deviceId }
                assertTrue("Device found", foundDevice != null)
            }
        }
    }

    @Test
    fun `add 3 devices and get the list`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run {transaction ->
                val deviceId1 = DeviceId("SomeId1")
                val ownerName1 = "owner1"
                val ownerMobile1 = 912345678L
                val deviceId2 = DeviceId("SomeId2")
                val ownerName2 = "owner2"
                val ownerMobile2 = 912345679L
                val deviceId3 = DeviceId("SomeId3")
                val ownerName3 = "owner3"
                val ownerMobile3 = 912345670L

                val devicesRepo = transaction.repository
                val device1 = Device(deviceId1, ownerName1, ownerMobile1)
                val device2 = Device(deviceId2, ownerName2, ownerMobile2)
                val device3 = Device(deviceId3, ownerName3, ownerMobile3)

                devicesRepo.addDevice(device1)
                devicesRepo.addDevice(device2)
                devicesRepo.addDevice(device3)

                val devices = devicesRepo.getAllDevices()
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