package pt.isel.iot_data_server.repo.static_content

import pt.isel.iot_data_server.utils.testWithTransactionManagerAndRollback



import org.junit.jupiter.api.Test;
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.utils.generateRandomEmail
import pt.isel.iot_data_server.utils.generateRandomName
import pt.isel.iot_data_server.utils.generateRandomPassword
import kotlin.random.Random


class UserRepoTests {

    @Test
    fun `add user and get`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run {transaction ->
                val usersRepo = transaction.repository
                val userInfo = UserInfo(generateRandomName(), generateRandomPassword(), generateRandomEmail())
                val user = User(Random.nextInt(), userInfo)
                usersRepo.createUser(user)
                val users = usersRepo.getAllUsers()
                assert(users.size == 1)
                assert(users[0] == user)
            }
        }
    }

    @Test
    fun `add user`(){


    }

}