package pt.isel.iot_data_server

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.SEED
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.utils.generateRandomMobileNumber
import pt.isel.iot_data_server.utils.generateRandomName
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback
import java.util.*

@SpringBootTest
class DeviceServiceTests {
	@Test
	fun `generate device ids`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = DeviceService(transactionManager)
			repeat(1000) { service.generateDeviceId() }
		}
	}
	@Test
	fun `generate device ids, and create Devices`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			// will use a different seed, each nanosecond
			val service = DeviceService(transactionManager, SEED.NANOSECOND)
			repeat(1000) {
				val deviceId = service.generateDeviceId()
				val ownerName = generateRandomName()
				val ownerMobile = generateRandomMobileNumber()
				val device = Device(deviceId, ownerName, ownerMobile)

				val result = service.addDevice(device)
				assert(result is Either.Right)

				Thread.sleep(1) // sleep for 1 millisecond, so that the seed changes
			}
		}
	}

	//MESSAGE: There is not much to test here
	/*
	@Test
	fun `create device`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = DeviceService(transactionManager)
			val device = Device(DeviceId(UUID.randomUUID()), name, mobile)

			service.addDevice(device)

			val devices = service.getAllDevices()
			assertTrue("Device was created", devices.any { it.deviceId == device.deviceId })
		}
	}

	@Test
	fun `create multiple devices`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = DeviceService(transactionManager)
			val device1 = Device(DeviceId(UUID.randomUUID()), name, mobile)
			val device2 = Device(DeviceId(UUID.randomUUID()), name, mobile)
			val device3 = Device(DeviceId(UUID.randomUUID()), name, mobile)

			service.addDevice(device1)
			service.addDevice(device2)
			service.addDevice(device3)

			val devices = service.getAllDevices()
			assertTrue("Device was not created", devices.any { it.deviceId == device1.deviceId })
			assertTrue("Device was not created", devices.any { it.deviceId == device2.deviceId })
			assertTrue("Device was not created", devices.any { it.deviceId == device3.deviceId })
		}
	}
*/
}
