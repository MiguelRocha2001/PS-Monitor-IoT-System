package pt.isel.iot_data_server.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.iot_data_server.repo.time_series_repo.deleteAllDeviceRecords
import pt.isel.iot_data_server.utils.generateRandomEmail
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback

@SpringBootTest
class DeviceServiceTests {

    @BeforeEach
    fun `remove all devices`() {
        deleteAllDeviceRecords()
    }

	@Test
	fun `Create valid Device`() {
		testWithTransactionManagerAndRollback {
			val (deviceService, userService) = getNewDeviceAndUserService(it)
			val userID = createRandomUser(userService)
			val ownerEmail = generateRandomEmail()

			val result = deviceService.addDevice(userID, ownerEmail)
			assertTrue(result is Either.Right)
			result as Either.Right

			assertTrue(deviceService.existsDevice(result.value))

			val device = deviceService.getDeviceByIdOrNull(result.value)

			assertTrue(device != null)
			assertTrue(device!!.deviceId == result.value)
			assertTrue(device.ownerEmail == ownerEmail)

			assertTrue(deviceService.belongsToUser(result.value, userID))
		}
	}

	@Test
	fun `Device exists`() {
		testWithTransactionManagerAndRollback {
			val (deviceService, userService) = getNewDeviceAndUserService(it)
			val userID = createRandomUser(userService)
			val ownerEmail = generateRandomEmail()

			val result = deviceService.addDevice(userID, ownerEmail)
			assertTrue(result is Either.Right)
			result as Either.Right

			assertTrue(deviceService.existsDevice(result.value))
		}
	}

	@Test
	fun `Invalid device doesnt exist`() {
		testWithTransactionManagerAndRollback {
			val (deviceService, _) = getNewDeviceAndUserService(it)

			assertTrue(deviceService.existsDevice("invalidDeviceId").not())
		}
	}

    @Test
    fun `generate device ids`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val (deviceService, _) = getNewDeviceAndUserService(transactionManager)
            repeat(1000) { deviceService.generateDeviceId() }
        }
    }

	@Test
	fun `Create invalid Device`(){
		testWithTransactionManagerAndRollback {
			val (deviceService, userService) = getNewDeviceAndUserService(it)

			val userId = createRandomUser(userService)

			val invalidEmail = "invalidEmail"
			val result = deviceService.addDevice(userId, invalidEmail)
			assertTrue(result is Either.Left)
		}
	}

	@Test
	fun `Get devices filtered by alert email`() {
		testWithTransactionManagerAndRollback {
			val (deviceService, userService) = getNewDeviceAndUserService(it)

			val userId = createRandomUser(userService)

			val deviceAlertEmail1 = "some_alert_email1@gmail.com"
			val deviceAlertEmail2 = "some_alert_email2@gmail.com"
			deviceService.addDevice(userId, deviceAlertEmail1)
			deviceService.addDevice(userId, deviceAlertEmail1)
			deviceService.addDevice(userId, deviceAlertEmail2)
			deviceService.addDevice(userId, deviceAlertEmail1)
			deviceService.addDevice(userId, deviceAlertEmail2)

			val devicesFound1 = deviceService.getUserDevices(userId, deviceAlertEmail = deviceAlertEmail1)
			assertTrue { devicesFound1 is Either.Right }
			devicesFound1 as Either.Right
			val devicesFound2 = deviceService.getUserDevices(userId, deviceAlertEmail = deviceAlertEmail2)
			assertTrue { devicesFound2 is Either.Right }
			devicesFound2 as Either.Right

			assertTrue(devicesFound1.value.size == 3)
			assertTrue(devicesFound2.value.size == 2)
		}
	}

	@Test
	fun `Get Device filtered by invalid email`(){
		testWithTransactionManagerAndRollback {
			val (deviceService, userService) = getNewDeviceAndUserService(it)

			val userId = createRandomUser(userService)

			val deviceAlertEmail = generateRandomEmail()
			deviceService.addDevice(userId, deviceAlertEmail)

			val alertEmail = generateRandomEmail() + "incorrect"
			val deviceFound = deviceService.getUserDevices(userId, deviceAlertEmail = alertEmail) as Either.Right
			assertTrue(deviceFound.value.isEmpty())
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
				val devicesResult = deviceService.getUserDevices(userId)
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

	@Test
	fun `Get valid Device by id`(){
		testWithTransactionManagerAndRollback {
			val (deviceService, userService) = getNewDeviceAndUserService(it)

			val userId = createRandomUser(userService)

			val deviceAlertEmail = generateRandomEmail()
			val res = deviceService.addDevice(userId, deviceAlertEmail)
			assertTrue(res is Either.Right)
			res as Either.Right

			val deviceFound = deviceService.getDeviceByIdOrNull(res.value)
			assertTrue(deviceFound != null)
		}
	}

	@Test
	fun `Get all devices `() {
		testWithTransactionManagerAndRollback {
			val (deviceService, userService) = getNewDeviceAndUserService(it)

			val userId1 = createRandomUser(userService)
			val userId2 = createRandomUser(userService)

			deviceService.addDevice(userId1, "some_alert_email1@gmail.com")
			deviceService.addDevice(userId2, "some_alert_email2@gmail.com")
			deviceService.addDevice(userId2, "some_alert_email3@gmail.com")
			deviceService.addDevice(userId2, "some_alert_email4@gmail.com")
			deviceService.addDevice(userId1, "some_alert_email5@gmail.com")
			deviceService.addDevice(userId2, "some_alert_email6@gmail.com")
			deviceService.addDevice(userId2, "some_alert_email7@gmail.com")

			val res = deviceService.getAllDevices()
			assertTrue(res is Either.Right)
			res as Either.Right
			assertEquals(7, res.value.size)

			val res2 = deviceService.getUserDevices(userId1)
			assertTrue(res2 is Either.Right)
			res2 as Either.Right
			assertEquals(2, res2.value.size)
			assertTrue(res.value.any { it.ownerEmail == "some_alert_email1@gmail.com" })
			assertTrue(res.value.any { it.ownerEmail == "some_alert_email5@gmail.com" })

			val res3 = deviceService.getUserDevices(userId2)
			assertTrue(res3 is Either.Right)
			res3 as Either.Right
			assertEquals(5, res3.value.size)
			assertTrue(res.value.any { it.ownerEmail == "some_alert_email2@gmail.com" })
			assertTrue(res.value.any { it.ownerEmail == "some_alert_email3@gmail.com" })
			assertTrue(res.value.any { it.ownerEmail == "some_alert_email4@gmail.com" })
			assertTrue(res.value.any { it.ownerEmail == "some_alert_email6@gmail.com" })
			assertTrue(res.value.any { it.ownerEmail == "some_alert_email7@gmail.com" })
		}
	}

	@Test
	fun `Get Devices filtered by id `() {
		testWithTransactionManagerAndRollback {
			val (deviceService, userService) = getNewDeviceAndUserService(it)

			val userId = createRandomUser(userService)
			val res1 = deviceService.addDevice(userId, "some_alert_email1@gmail.com") as Either.Right
			deviceService.addDevice(userId, "some_alert_email1@gmail.com") as Either.Right
			deviceService.addDevice(userId, "some_alert_email1@gmail.com") as Either.Right

			repeat(4) {
				val res2 = deviceService.getUserDevices(userId, deviceIdChunk = res1.value[it].toString())
				assertTrue(res2 is Either.Right)
				res2 as Either.Right
				assertTrue(res2.value.any { it.ownerEmail == "some_alert_email1@gmail.com" })

				val res3 = deviceService.getDeviceCount(userId, null, res1.value[it].toString())
				assertTrue(res3 is Either.Right)
				res3 as Either.Right
				assertTrue(res3.value >= 1)
			}
		}
	}

	@Test
	fun `Get Devices filtered by invalid id `() {
		testWithTransactionManagerAndRollback {
			val (deviceService, userService) = getNewDeviceAndUserService(it)

			val userId = createRandomUser(userService)
			val res1 = deviceService.addDevice(userId, "some_alert_email1@gmail.com") as Either.Right
			deviceService.addDevice(userId, "some_alert_email1@gmail.com") as Either.Right
			deviceService.addDevice(userId, "some_alert_email1@gmail.com") as Either.Right

			val res2 = deviceService.getUserDevices(userId, deviceIdChunk = "invalid")
			assertTrue(res2 is Either.Right)
			res2 as Either.Right
			assertTrue(res2.value.isEmpty())

			val res3 = deviceService.getDeviceCount(userId, null, "invalid")
			assertTrue(res3 is Either.Right)
			res3 as Either.Right
			assertTrue(res3.value == 0)
		}
	}

	@Test
	fun `Delete all devices `() {
		testWithTransactionManagerAndRollback {
			val (deviceService, userService) = getNewDeviceAndUserService(it)

			val userId1 = createRandomUser(userService)
			val userId2 = createRandomUser(userService)

			deviceService.addDevice(userId1, "some_alert_email1@gmail.com")
			deviceService.addDevice(userId2, "some_alert_email1@gmail.com")
			deviceService.addDevice(userId1, "some_alert_email1@gmail.com")
			deviceService.addDevice(userId2, "some_alert_email1@gmail.com")
			deviceService.addDevice(userId1, "some_alert_email1@gmail.com")
			deviceService.addDevice(userId2, "some_alert_email1@gmail.com")
			deviceService.addDevice(userId1, "some_alert_email1@gmail.com")

			val res = deviceService.getAllDevices()
			assertTrue(res is Either.Right)
			res as Either.Right
			assertEquals(7, res.value.size)

			deviceService.deleteAllDevices()

			val res2 = deviceService.getAllDevices()
			assertTrue(res2 is Either.Right)
			res2 as Either.Right
			assertEquals(0, res2.value.size)
		}
	}
}
