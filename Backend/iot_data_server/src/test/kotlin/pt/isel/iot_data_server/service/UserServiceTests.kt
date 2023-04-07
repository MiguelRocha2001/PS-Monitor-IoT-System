package pt.isel.iot_data_server.service

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.service.user.UserService
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback

@SpringBootTest
class UserServiceTests {
	@Test
	fun `create user`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = UserService(transactionManager)
			val username = "userGood"
			val password = "LKMSDOVCJ09Jouin09JN@"
			val email = "testSubject@email.com"
			val mobile = "123456789"
			val newUser = UserInfo(username, password,email,mobile)
			service.createUser(newUser)
			val users = service.getAllUsers()
			//assertValues of every property of student with the expected values
			assertTrue("User was created", users.any { it.userInfo.username == username })
			assertTrue("User was created", users.any { it.userInfo.password == password })
			assertTrue("User was created", users.any { it.userInfo.email == email })
			assertTrue("User was created", users.any { it.userInfo.mobile == mobile })
		}
	}

	@Test
	fun `create user with invalid short name`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = UserService(transactionManager)
			val username = "us"
			val password = "LKMSDOVCJ09Jouin09JN@"
			val email = "testSubject@email.com"
			val mobile = "123456789"
			try {
				val newInvalidUser = UserInfo(username, password,email,mobile)
				assert(false) //should not reach this line
			}catch (e: IllegalArgumentException){
				assertTrue("User was not created", e.message == "Username must be at least 5 characters long")
				assert(true)
			}

		}

	}

	@Test
	fun `create user with invalid email`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = UserService(transactionManager)
			val username = "userGood"
			val password = "LKMSDOVCJ09Jouin09JN@"
			val email = "testSubjectemail.com"
			val mobile = "123456789"
			try {
				val newInvalidUser = UserInfo(username, password,email,mobile)
				assert(false) //should not reach this line
			}catch (e: IllegalArgumentException){
				assertTrue("User was not created", e.message == "Invalid email address")
				assert(true)
			}
		}
	}

	@Test
	fun `create user with invalid mobile`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = UserService(transactionManager)
			val username = "userGood"
			val password = "LKMSDOVCJ09Jouin09JN@"
			val email = "testSubject@email.com"
			val mobile = "123"
			try {
				val newInvalidUser = UserInfo(username, password, email, mobile)
				assert(false) //should not reach this line
			} catch (e: IllegalArgumentException) {
				assertTrue("User was not created", e.message == "Invalid mobile number")
				assert(true)
			}
		}
	}


	@Test
	fun `create multiple users`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = UserService(transactionManager)
			val username1 = "userGood1"
			val password1 = "LKMSDOVCJ09Jouin09JN@1"
			val email1 = "testSubject1@email.com"
			val mobile1 = "123466666"
			val newUser1 = UserInfo(username1, password1,email1,mobile1)


			val username2 = "userGood2"
			val password2 = "LKMSDOVCJ09Jouin09JN@"
			val email2 = "testSubject2@email.com"
			val mobile2 = "123435559"
			val newUser2 = UserInfo(username2, password2,email2,mobile2)

			val username3 = "userGood3"
			val password3 = "LKMSDOVCJ09Jouin09JN@"
			val email3 = "testSubject3@email.com"
			val mobile3 = "123499999"
			val newUser3 = UserInfo(username3, password3,email3,mobile3)

			service.createUser(newUser1)
			service.createUser(newUser2)
			service.createUser(newUser3)

			val users = service.getAllUsers()
			//we can use mobile because it is unique for each user
			assertTrue("User was not created", users.any { it.userInfo.mobile == mobile1 })
			assertTrue("User was not created", users.any { it.userInfo.mobile == mobile2 })
			assertTrue("User was not created", users.any { it.userInfo.mobile == mobile3 })
		}
	}

	@Test //FIXME ;) chanhe this test
	fun `create multiple users with same email`() {
		testWithTransactionManagerAndRollback { transactionManager ->
			val service = UserService(transactionManager)
			val username1 = "userGood1"
			val password1 = "LKMSDOVCJ09Jouin09JN@1"
			val email1 = "sameemail@email.com"
			val mobile1 = "123466666"
			val newUser1 = UserInfo(username1, password1,email1,mobile1)


			val username2 = "userGood2"
			val password2 = "LKMSDOVCJ09Jouin09JN@"
			val email2 = "sameemail@email.com"
			val mobile2 = "123435559"
			val newUser2 = UserInfo(username2, password2,email2,mobile2)


			service.createUser(newUser1)
			service.createUser(newUser2)

			val users = service.getAllUsers()
			//we can use mobile because it is unique for each user
			assertTrue("User was not created", users.any { it.userInfo.mobile == mobile1 })
			assertTrue("User was not created", users.any { it.userInfo.mobile == mobile2 })
		}
	}
}
