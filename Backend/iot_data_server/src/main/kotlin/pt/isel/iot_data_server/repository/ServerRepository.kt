package pt.isel.iot_data_server.repository

import pt.isel.iot_data_server.domain.*

interface ServerRepository {

    fun createUser(user: User)
    fun getAllUsers(): List<User>
    fun getUserByToken(token: String): User?
    fun addToken(userId: Int, token: String)
    fun addDevice(device: Device)
    fun getAllDevices(): List<Device>
    fun getPhRecords(deviceId: DeviceId): List<PhRecord>
    fun savePhRecord(deviceId: DeviceId, phRecord: PhRecord)
    fun getTemperatureRecords(deviceId: DeviceId): List<TemperatureRecord>
    fun saveTemperatureRecord(deviceId: DeviceId, temperatureRecord: TemperatureRecord)
    fun exists(user: User): Boolean
    fun getUserByUsername(username: String): User
}