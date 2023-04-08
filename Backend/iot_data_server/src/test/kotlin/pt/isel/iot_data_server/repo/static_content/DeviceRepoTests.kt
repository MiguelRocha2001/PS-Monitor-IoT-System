package pt.isel.iot_data_server.repo.static_content

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
                val devicesRepo = transaction.repository
                val device = Device(DeviceId("4521087288"), "exampleEmail@pront.com", 934829492)
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
                val devicesRepo = transaction.repository
                val device1 = Device(DeviceId("4521087288"), "exampleEmail@pront.com", 944829492)
                val device2 = Device(DeviceId("4533387288"), "exampleEmail2@pront.com", 934829492)
                val device3 = Device(DeviceId("4521555288"), "exampleEmail3@pront.com", 924829492)
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