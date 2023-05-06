package pt.isel.iot_data_server.security

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.assertFalse
import org.springframework.test.util.AssertionErrors.assertTrue
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.service.user.Role
import pt.isel.iot_data_server.service.user.SaltPasswordOperations
import pt.isel.iot_data_server.service.user.UserService
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback
import java.util.*

class SaltTests {
    private val role = Role.USER

    @Test
    fun `verify if two equal passwords are stored the same`(){
        testWithTransactionManagerAndRollback { transactionManager ->
            val salt = SaltPasswordOperations(transactionManager)
            val service = UserService(transactionManager,salt)

            //create user
            val pass = "LKMSDOVCJ09Jouin09JN@"
            val newUser = UserInfo("userGood", pass,"testSubject@email.com", role)
            service.createUser(newUser)

            val newUser2 = UserInfo("userGood2", pass,"testSubject2@email.com", role)
            service.createUser(newUser2)


            val userStoredPassword = service.getUserByEmailAddress(newUser.email)?.userInfo?.password
            val user2StoredPassword = service.getUserByEmailAddress(newUser2.email)?.userInfo?.password

            assertFalse("Password is not the same", userStoredPassword == user2StoredPassword)
        }


    }


    @Test
    fun `verify correct password`(){
        testWithTransactionManagerAndRollback { transactionManager ->
            val salt = SaltPasswordOperations(transactionManager)
            val service = UserService(transactionManager,salt)

            //create user
            val pass = "LKMSDOVCJ09Jouin09JN@"
            val newUser = UserInfo("userGood", pass,"testSubject@email.com", role)
            service.createUser(newUser)

            //stored user password
            val user = service.getUserByEmailAddress(newUser.email)?.userInfo
            if(user == null) throw Exception("User not found")

            //verify password
            val resultOfVerify = salt.verifyPassword(user.username,pass)//storedPassword is not null
            assertTrue("Password is correct", resultOfVerify)
        }
    }

    @Test
    fun `verify incorrect password`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val salt = SaltPasswordOperations(transactionManager)
            val service = UserService(transactionManager,salt)

            //create user
            val pass = "LKMSDOVCJ09Jouin09JN@"
            val newUser = UserInfo("userGood", pass,"testSubject@email.com", role)
            service.createUser(newUser)

            //stored user password
            val user = service.getUserByEmailAddress(newUser.email)?.userInfo
            if(user == null) throw Exception("User not found")

            val fakePass = "LKMSDOVCJ09Jouin0fake"
            //verify password
            val resultOfVerify = salt.verifyPassword(user.username,fakePass)//storedPassword is not null
            assertFalse("Password is correct", resultOfVerify)
        }
    }

    @Test
    fun `save a valid salt`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val saltPasswordOperations = SaltPasswordOperations(transactionManager)
            val id = UUID.randomUUID().toString()
            val password = "foefmefew43ok@skdkK"
            saltPasswordOperations.saltAndHashPass(password,id)
            val salt = saltPasswordOperations.getSalt(id)
            Assertions.assertTrue(salt.isNotEmpty())
        }
    }

}