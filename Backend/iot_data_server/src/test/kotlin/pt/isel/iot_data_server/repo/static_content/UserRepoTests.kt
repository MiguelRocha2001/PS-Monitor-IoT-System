package pt.isel.iot_data_server.repo.static_content


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.service.user.Role
import pt.isel.iot_data_server.utils.generateRandomEmail
import pt.isel.iot_data_server.utils.generateRandomName
import pt.isel.iot_data_server.utils.generateRandomPassword
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback
import java.util.*

/*
class UserRepoTests {
    private val role = Role.USER

    @Test
    fun `add user and get`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val usersRepo = transaction.userRepo

                val userId = UUID.randomUUID().toString()
                val userInfo = UserInfo(generateRandomName(), generateRandomPassword(), generateRandomEmail(), role)

                val user = User(userId, userInfo)
                usersRepo.createUser(user)
                val users = usersRepo.getAllUsers()

                assertEquals(1, users.size)
                assertEquals(user, users[0])
            }
        }
    }

    @Test
    fun `add user`(){


    }
}*/