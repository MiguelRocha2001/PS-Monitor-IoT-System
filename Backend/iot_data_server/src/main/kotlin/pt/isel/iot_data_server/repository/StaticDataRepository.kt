package pt.isel.iot_data_server.repository

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.User

interface StaticDataRepository {//todo all the sensor data needs to go away

    fun createUser(user: User)
    fun getAllUsers(): List<User>
    fun getUserByToken(token: String): User?
    fun addToken(userId: String, token: String)
    fun addDevice(device: Device)
    fun getAllDevices(): List<Device>
    fun deleteDevice(deviceId: DeviceId)
    fun removeAllDevices()
    fun existsUsername(username: String): Boolean
    fun existsEmail(email: String): Boolean
    fun getUserByUsernameOrNull(username: String): User?
    fun saveSalt(userId: String, salt: String)
    fun getSalt(userId: String): String
    fun getUserByEmailAddressOrNull(email: String): User?

    fun getDevicesByOwnerEmail(email: String): List<Device>
    fun deleteAllUsers()
}