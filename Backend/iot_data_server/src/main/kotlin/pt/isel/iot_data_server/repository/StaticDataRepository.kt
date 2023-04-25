package pt.isel.iot_data_server.repository

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.User

interface StaticDataRepository {//todo all the sensor data needs to go away
    fun createUser(user: User)
    fun getAllUsers(): List<User>
    fun getUserByToken(token: String): User?
    fun getUserByIdOrNull(userId: String): User?
    fun createToken(userId: String, token: String)
    fun createDevice(userId: String, device: Device)
    fun getAllDevices(page: Int? = null, limit: Int? = null): List<Device> // TODO: test with params
    fun getAllDevices(userId: String, page: Int? = null, limit: Int? = null): List<Device> // TODO: test with params
    fun deleteDevice(deviceId: String)
    fun removeAllDevices()
    fun existsUsername(username: String): Boolean
    fun existsEmail(email: String): Boolean
    fun getUserByUsernameOrNull(username: String): User?
    fun saveSalt(userId: String, salt: String)
    fun getSalt(userId: String): String
    fun getUserByEmailAddressOrNull(email: String): User?
    fun getDevicesByOwnerEmail(email: String): List<Device>
    fun deleteAllUsers()
    fun getDeviceById(deviceId: String): Device?
    fun deleteAllTokens()
    fun deleteAllDevices()

    fun deviceCount(userId: String): Int // TODO: test this
}