package pt.isel.iot_data_server.repository

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceLogRecord
import pt.isel.iot_data_server.domain.SensorErrorRecord

interface DeviceDataRepository {
    fun createDevice(userId: String, device: Device)
    fun getAllDevices(page: Int? = null, limit: Int? = null): List<Device> // TODO: test with params
    fun getAllDevices(userId: String, page: Int? = null, limit: Int? = null): List<Device> // TODO: test with params
    fun deleteDevice(deviceId: String)
    fun removeAllDevices()
    fun getDevicesByOwnerEmail(email: String): List<Device>
    fun getDeviceById(deviceId: String): Device?
    fun deleteAllDevices()
    fun deviceCount(userId: String): Int // TODO: test this
    fun saveSensorErrorRecord(deviceId: String, sensorErrorRecord: SensorErrorRecord)
    fun getSensorErrorRecords(deviceId: String): List<SensorErrorRecord>
    fun getAllSensorErrorRecords(): List<SensorErrorRecord>
    fun saveDeviceLogRecord(deviceId: String, deviceLogRecord: DeviceLogRecord)
    fun getDeviceLogRecords(deviceId: String): List<DeviceLogRecord>
    fun getAllDeviceLogRecords(): List<DeviceLogRecord>
    fun getDevicesFilteredById(deviceId: String): List<Device>
}