package pt.isel.iot_data_server.repository

import org.jdbi.v3.core.Handle
import pt.isel.iot_data_server.domain.*

interface ServerRepository {

    fun createUser(username: String, password: String)
    fun getUserByToken(token: String): User?
    fun createToken(username: String, password: String)
    fun addDevice(device: Device)
    fun getPhRecords(deviceId: DeviceId): List<PhRecord>
    fun savePhRecord(deviceId: DeviceId, phRecord: PhRecord)
    fun getTemperatureRecords(deviceId: DeviceId): List<TemperatureRecord>
    fun saveTemperatureRecord(deviceId: DeviceId, temperatureRecord: TemperatureRecord)
}