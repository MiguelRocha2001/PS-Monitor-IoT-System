package pt.isel.iot_data_server.repository

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceWakeUpLog
import pt.isel.iot_data_server.domain.SensorErrorRecord

interface DeviceDataRepository {
    fun createDevice(userId: String, device: Device)
    fun getAllDevices(page: Int? = null, limit: Int? = null): List<Device> // TODO: test with params
    fun getAllDevicesByUserId(userId: String, page: Int? = null, limit: Int? = null): List<Device> // TODO: test with params
    fun deleteDevice(deviceId: String)
    fun removeAllDevices()
    fun getDevicesByOwnerEmail(email: String): List<Device>
    fun getDeviceById(deviceId: String): Device?
    fun deleteAllDevices()
    fun deviceCount(userId: String): Int // TODO: test this
    @Deprecated("Discontinued")
    fun saveSensorErrorRecord(deviceId: String, sensorErrorRecord: SensorErrorRecord)
    @Deprecated("Discontinued")
    fun getSensorErrorRecords(deviceId: String): List<SensorErrorRecord>
    @Deprecated("Discontinued")
    fun getAllSensorErrorRecords(): List<SensorErrorRecord>
    fun saveDeviceLogRecord(deviceId: String, deviceWakeUpLog: DeviceWakeUpLog)
    fun getDeviceLogRecords(deviceId: String): List<DeviceWakeUpLog>
    fun getAllDeviceLogRecords(): List<DeviceWakeUpLog>
    fun getDevicesFilteredById(id:String, userId: String, page: Int?, limit: Int?): List<Device>
    fun getCountOfDevicesFilteredById(userId:String,deviceId: String): Int
}