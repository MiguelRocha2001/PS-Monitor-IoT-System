package pt.isel.iot_data_server

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.service.DeviceService
import pt.isel.iot_data_server.service.UserService
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback
import java.util.*

@SpringBootTest
class DeviceServiceTests {
	@Test
	fun `create device`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = DeviceService(transactionManager)
			val device = Device(DeviceId(UUID.randomUUID()))

			service.createDevice(device)

			val devices = service.getAllDevices()
			assertTrue("Device was not created", devices.any { it.deviceId == device.deviceId })
		}
	}

	@Test
	fun `create multiple users`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = DeviceService(transactionManager)
			val device1 = Device(DeviceId(UUID.randomUUID()))
			val device2 = Device(DeviceId(UUID.randomUUID()))
			val device3 = Device(DeviceId(UUID.randomUUID()))

			service.createDevice(device1)
			service.createDevice(device2)
			service.createDevice(device3)

			val devices = service.getAllDevices()
			assertTrue("Device was not created", devices.any { it.deviceId == device1.deviceId })
			assertTrue("Device was not created", devices.any { it.deviceId == device2.deviceId })
			assertTrue("Device was not created", devices.any { it.deviceId == device3.deviceId })
		}
	}

	@Test
	fun `create existing user`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = DeviceService(transactionManager)
			val device = Device(DeviceId(UUID.randomUUID()))

			service.createDevice(device)

			val devices = service.getAllDevices()
			assertTrue("Device was not created", devices.any { it.deviceId == device.deviceId })

			assertThrows<Exception> { service.createDevice(device) }
		}
	}
}
