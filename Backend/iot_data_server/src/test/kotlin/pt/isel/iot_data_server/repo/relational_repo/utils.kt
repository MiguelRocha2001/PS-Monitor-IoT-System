package pt.isel.iot_data_server.repo.relational_repo

import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.domain.UserInfo
import pt.isel.iot_data_server.repository.UserDataRepository
import pt.isel.iot_data_server.service.user.Role
import pt.isel.iot_data_server.utils.generateRandomEmail
import java.util.*

internal fun createUser(usersRepo: UserDataRepository, email: String): User {
    val userId = UUID.randomUUID().toString()
    val userInfo = UserInfo(email, Role.USER)

    val user = User(userId, userInfo)
    usersRepo.createUser(user)

    return user
}