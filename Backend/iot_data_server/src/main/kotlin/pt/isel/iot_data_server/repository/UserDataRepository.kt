package pt.isel.iot_data_server.repository

import pt.isel.iot_data_server.domain.User

interface UserDataRepository {
    fun createUser(user: User)
    fun getAllUsers(): List<User>
    fun getUserByToken(token: String): User?
    fun getUserByIdOrNull(userId: String): User?
    fun createToken(userId: String, token: String)
    fun existsEmail(email: String): Boolean
    fun getUserByEmailOrNull(email: String): User?
    fun saveSalt(userId: String, salt: String)
    fun getSalt(userId: String): String
    fun getUserByEmailAddressOrNull(email: String): User?
    fun deleteAllUsers()
    fun deleteAllTokens()
    fun deleteUser(userId: String)
    fun addVerificationCode(email: String, code: String)
    fun getVerificationCode(email: String): String?
}