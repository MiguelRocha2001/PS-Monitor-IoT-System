package pt.isel.iot_data_server.repository

import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.service.user.Role

interface UserDataRepository {
    fun createUser(user: User)
    fun getAllUsers(): List<User>
    fun getAllUsersWithRole(role: Role): List<User> // TODO: test this
    fun getUserByToken(token: String): User?
    fun getUserByIdOrNull(userId: String): User?
    fun createToken(userId: String, token: String)
    fun deleteUserToken(userId: String) // TODO: test this
    fun getTokenFromUser(userId: String): String?
    @Deprecated("Use deleteTokenByUserId instead")
    fun deleteToken(token: String)
    fun existsEmail(email: String): Boolean
    fun getUserByEmailOrNull(email: String): User?
    fun deleteAllUsers(role: Role? = null)
    fun deleteAllTokens(role: Role? = null)
    fun deleteUser(userId: String)
    @Deprecated("Not used anymore")
    fun addVerificationCode(email: String, code: String)
    @Deprecated("Not used anymore")
    fun getVerificationCode(email: String): String?
    fun storePasswordAndSalt(userId: String, value: String, salt: String)
    fun getPasswordAndSalt(userId: String): Pair<String, String>
    fun deleteAllPasswords(role: Role? = null)
    fun deleteUserPassword(userId: String)
}