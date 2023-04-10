package pt.isel.iot_data_server.service

import ch.qos.logback.core.pattern.util.RegularEscapeUtil
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.SEED
import pt.isel.iot_data_server.repository.jdbi.JdbiServerRepository
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
	fun `create a device correctly`() {
		testWithTransactionManagerAndRollback {

			val service = DeviceService(it, SEED.NANOSECOND)

			service.removeAllDevices()// just in case there are any devices in the database

			var devices = service.getAllDevices()
			assertTrue(devices.isEmpty())

			val ownerEmail = generateRandomEmail()
			val result = service.addDevice(ownerEmail)
			assertTrue(result is Either.Right)

			devices = service.getAllDevices()
			assertTrue(devices.size == 1)
			assertTrue(devices[0].ownerEmail == ownerEmail)
		}
	}

	@Test
	fun `create invalid device`(){
		testWithTransactionManagerAndRollback {
			val service = DeviceService(it, SEED.NANOSECOND)
			service.removeAllDevices()// just in case there are any devices in the database
			val result = service.addDevice("")
			assertTrue(result is Either.Left)
		}
	}

	@Test
	fun `get valid device by email`(){
		testWithTransactionManagerAndRollback {
			val service = DeviceService(it, SEED.NANOSECOND)
			service.removeAllDevices()// just in case there are any devices in the database
			val ownerEmail = generateRandomEmail()
			service.addDevice(ownerEmail)
			service.addDevice(ownerEmail)
			service.addDevice(ownerEmail)
			service.addDevice(ownerEmail)
			service.addDevice(ownerEmail)
			val deviceFound = service.getDevicesByOwnerEmail(ownerEmail)
			assertTrue(deviceFound.size == 5)
		}
	}

	@Test
	fun `get device by invalid email`(){
		testWithTransactionManagerAndRollback {
			val service = DeviceService(it, SEED.NANOSECOND)
			service.removeAllDevices()// just in case there are any devices in the database
			val ownerEmail = generateRandomEmail()+"incorrect"
			val deviceFound = service.getDevicesByOwnerEmail(ownerEmail)
			assertTrue(deviceFound.isEmpty())
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
