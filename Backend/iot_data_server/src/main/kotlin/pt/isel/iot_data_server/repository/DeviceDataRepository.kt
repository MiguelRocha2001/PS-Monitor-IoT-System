package pt.isel.iot_data_server.repository

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceWakeUpLog
import pt.isel.iot_data_server.domain.SensorErrorRecord
import java.sql.Timestamp

interface DeviceDataRepository {
    fun createDevice(userId: String, device: Device)
    fun getAllDevices(page: Int? = null, limit: Int? = null): List<Device> // TODO: test with params
    fun getAllDevicesByUserId(
        userId: String,
        page: Int? = null,
        limit: Int? = null,
        deviceAlertEmail: String? = null,
        deviceIdChunk: String? = null
    ): List<Device> // TODO: test with params
    fun deviceCount(
        userId: String,
        deviceAlertEmail: String? = null,
        deviceIdChunk: String? = null
    ): Int
    fun deleteDevice(deviceId: String)
    @Deprecated("Discontinued")
    fun getDevicesByAlertEmail(email: String): List<Device>
    fun getDeviceById(deviceId: String): Device?
    fun deleteAllDevices()
    @Deprecated("Discontinued")
    fun saveSensorErrorRecord(deviceId: String, sensorErrorRecord: SensorErrorRecord)
    @Deprecated("Discontinued")
    fun getSensorErrorRecords(deviceId: String): List<SensorErrorRecord>
    @Deprecated("Discontinued")
    fun getAllSensorErrorRecords(): List<SensorErrorRecord>
    fun createDeviceWakeUpLogs(deviceId: String, deviceWakeUpLog: DeviceWakeUpLog)
    fun getDeviceWakeUpLogByDeviceId(deviceId: String, timestamp: Timestamp): DeviceWakeUpLog?
    fun getDeviceWakeUpLogs(deviceId: String): List<DeviceWakeUpLog>
    fun updateDeviceWakeUpLogs(deviceId: String, deviceWakeUpLog: DeviceWakeUpLog)
    @Deprecated("Discontinued")
    fun getDevicesFilteredById(id:String, userId: String, page: Int?, limit: Int?): List<Device>
    fun getCountOfDevicesFilteredById(userId:String,deviceId: String): Int
}