package pt.isel.iot_data_server.repo

import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback
import java.util.*

class DeviceRepoTests {
//FIXME URGENTE METER BASE DE DADOS DE TESTS
    @Test
    fun `add device`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run {transaction ->
                val devicesRepo = transaction.repository
                val device = Device(DeviceId(UUID.randomUUID()), name, mobile)
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
                val device1 = Device(DeviceId(UUID.randomUUID()), name, mobile)
                val device2 = Device(DeviceId(UUID.randomUUID()), name, mobile)
                val device3 = Device(DeviceId(UUID.randomUUID()), name, mobile)
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