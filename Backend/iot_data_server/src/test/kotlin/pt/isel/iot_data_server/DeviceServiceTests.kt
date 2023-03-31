package pt.isel.iot_data_server

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback
import java.util.*

@SpringBootTest
class DeviceServiceTests {
	//MESSAGE: There is not much to test here
	@Test
	fun `create device`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = DeviceService(transactionManager)
			val device = Device(DeviceId(UUID.randomUUID()))

			service.addDevice(device)

			val devices = service.getAllDevices()
			assertTrue("Device was created", devices.any { it.deviceId == device.deviceId })
		}
	}

	@Test
	fun `create multiple devices`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = DeviceService(transactionManager)
			val device1 = Device(DeviceId(UUID.randomUUID()))
			val device2 = Device(DeviceId(UUID.randomUUID()))
			val device3 = Device(DeviceId(UUID.randomUUID()))

			service.addDevice(device1)
			service.addDevice(device2)
			service.addDevice(device3)

			val devices = service.getAllDevices()
			assertTrue("Device was not created", devices.any { it.deviceId == device1.deviceId })
			assertTrue("Device was not created", devices.any { it.deviceId == device2.deviceId })
			assertTrue("Device was not created", devices.any { it.deviceId == device3.deviceId })
		}
	}

}
