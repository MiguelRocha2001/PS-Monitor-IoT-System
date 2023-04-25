package pt.isel.iot_data_server.service

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.iot_data_server.utils.deleteAllDeviceRecords
import pt.isel.iot_data_server.utils.generateRandomEmail
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback

@SpringBootTest
class DeviceServiceTests {

	@BeforeEach
	fun `remove all devices`() {
		deleteAllDeviceRecords()
	}
	@Test
	fun `generate device ids`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val (deviceService, _) = getNewDeviceAndUserService(transactionManager)
			repeat(1000) { deviceService.generateDeviceId() }
		}
	}

	@Test
	fun `create a device correctly`() {
		testWithTransactionManagerAndRollback {
			val (deviceService, userService) = getNewDeviceAndUserService(it)
			//service.removeAllDevices()// just in case there are any devices in the database

			val userID = createRandomUser(userService)
			var devicesResult = deviceService.getAllDevices(userID, page, limit)
			assertTrue(devicesResult is Either.Right && devicesResult.value.isEmpty())

			val ownerEmail = generateRandomEmail()
			val result = deviceService.addDevice(userID, ownerEmail)
			assertTrue(result is Either.Right)

			devicesResult = deviceService.getAllDevices(userID, page, limit)
			assertTrue(devicesResult is Either.Right && devicesResult.value.size == 1)
			devicesResult as Either.Right
			assertTrue(devicesResult.value[0].ownerEmail == ownerEmail)
		}
	}

	@Test
	fun `create invalid device`(){
		testWithTransactionManagerAndRollback {
			val (deviceService, userService) = getNewDeviceAndUserService(it)

			//	service.removeAllDevices()// just in case there are any devices in the database

			val userId = createRandomUser(userService)

			val result = deviceService.addDevice(userId, "")
			assertTrue(result is Either.Left)
		}
	}

	@Test
	fun `get valid device by email`(){
		testWithTransactionManagerAndRollback {
			val (deviceService, userService) = getNewDeviceAndUserService(it)

			//	service.removeAllDevices()// just in case there are any devices in the database

			val userId = createRandomUser(userService)

			val deviceOwnerEmail = generateRandomEmail()
			deviceService.addDevice(userId, deviceOwnerEmail)
			deviceService.addDevice(userId, deviceOwnerEmail)
			deviceService.addDevice(userId, deviceOwnerEmail)
			deviceService.addDevice(userId, deviceOwnerEmail)
			deviceService.addDevice(userId, deviceOwnerEmail)

			val deviceFound = deviceService.getDevicesByOwnerEmail(deviceOwnerEmail)
			assertTrue(deviceFound.size == 5)
		}
	}

	@Test
	fun `get device by invalid email`(){
		testWithTransactionManagerAndRollback {
			val (deviceService, userService) = getNewDeviceAndUserService(it)

			//	service.removeAllDevices()// just in case there are any devices in the database

			val userId = createRandomUser(userService)

			val deviceOwnerEmail = generateRandomEmail()
			deviceService.addDevice(userId, deviceOwnerEmail)

			//	service.removeAllDevices()// just in case there are any devices in the database

			val ownerEmail = generateRandomEmail()+"incorrect"
			val deviceFound = deviceService.getDevicesByOwnerEmail(ownerEmail)
			assertTrue(deviceFound.isEmpty())
		}
	}


	@Test
	fun `Create devices, and then assert if generated ids dont collide with already existent device ids`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val (deviceService, userService) = getNewDeviceAndUserService(transactionManager)
			val userId = createRandomUser(userService)
			// will use a different seed, each nanosecond
			repeat(30) {
				// creates a device
				val ownerEmail = generateRandomEmail()
				val result = deviceService.addDevice(userId, ownerEmail)
				assertTrue(result is Either.Right)

				// tries to generate a new device id, and asserts that it is unique
				val devicesResult = deviceService.getAllDevices(userId, page, limit)
				assertTrue(devicesResult is Either.Right)
				devicesResult as Either.Right
				val devices = devicesResult.value

				repeat(30) {
					val newDeviceId = deviceService.generateDeviceId()
					assertTrue(devices.none { it.deviceId == newDeviceId })
					Thread.sleep(1) // sleep for 1 millisecond, so that the seed changes
				}
			}
		}
	}

}
