package pt.isel.iot_data_server.repository

import pt.isel.iot_data_server.domain.*

interface StaticDataRepository {//todo all the sensor data needs to go away

    fun createUser(user: User)
    fun getAllUsers(): List<User>
    fun getUserByToken(token: String): User?
    fun addToken(userId: Int, token: String)
    fun addDevice(device: Device)
    fun getAllDevices(): List<Device>
    fun deleteDevice(deviceId: DeviceId)
    fun removeAllDevices(): Unit

  //  fun getPhRecords(deviceId: DeviceId): List<PhRecord>
 //   fun savePhRecord(deviceId: DeviceId, phRecord: PhRecord)
 //   fun getTemperatureRecords(deviceId: DeviceId): List<TemperatureRecord>
  //  fun saveTemperatureRecord(deviceId: DeviceId, temperatureRecord: TemperatureRecord)
    fun existsUsername(username: String): Boolean
    fun existsEmail(email: String): Boolean
    fun getUserByUsername(username: String): User
    fun saveSalt(userId: Int, salt: String)
    fun getSalt(userId: Int): String
    fun getUserByEmailAddress(email: String): User?

    fun getDevicesByOwnerEmail(email: String): List<Device>
}