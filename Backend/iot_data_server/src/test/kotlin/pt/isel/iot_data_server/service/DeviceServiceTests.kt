package pt.isel.iot_data_server.service

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.SEED
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.utils.generateRandomEmail
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback

@SpringBootTest
class DeviceServiceTests {
	@Test
	fun `generate device ids`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = DeviceService(transactionManager, SEED.NANOSECOND)
			repeat(1000) { service.generateDeviceId() }
		}
	}

	@Test
	fun `Create devices, and then assert if generated ids dont collide with already existent device ids`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			// will use a different seed, each nanosecond
			val service = DeviceService(transactionManager, SEED.NANOSECOND)
			repeat(30) {
				// creates a device
				val ownerEmail = generateRandomEmail()
				val result = service.addDevice(ownerEmail)
				assertTrue(result is Either.Right)

				// tries to generate a new device id, and asserts that it is unique
				val devices = service.getAllDevices()
				repeat(30) {
					val newDeviceId = service.generateDeviceId()
					assertTrue(devices.none { it.deviceId == newDeviceId })
					Thread.sleep(1) // sleep for 1 millisecond, so that the seed changes
				}
			}
		}
	}
}
