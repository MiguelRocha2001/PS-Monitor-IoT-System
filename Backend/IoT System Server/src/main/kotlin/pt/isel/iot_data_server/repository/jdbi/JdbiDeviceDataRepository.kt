package pt.isel.iot_data_server.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceWakeUpLog
import pt.isel.iot_data_server.domain.SensorErrorRecord
import pt.isel.iot_data_server.repository.DeviceDataRepository
import pt.isel.iot_data_server.repository.jdbi.mappers.DeviceMapper
import pt.isel.iot_data_server.repository.jdbi.mappers.toDevice

class JdbiDeviceDataRepository(
    private val handle: Handle
) : DeviceDataRepository {
    override fun createDevice(userId: String, device: Device) {
        handle.createUpdate(
            """
            insert into device (id, user_id, email) values (:id, :user_id, :email)
            """
        )
            .bind("id", device.deviceId)
            .bind("user_id", userId)
            .bind("email", device.ownerEmail)
            .execute()
    }

    override fun getAllDevices(page: Int?, limit: Int?): List<Device> {
        val offset = ((page ?: 1) - 1) * (limit ?: 10)
        return handle.createQuery("""
            select id, user_id, email
            from device 
            limit :limit 
            offset :offset
        """)
            .bind("limit", limit ?: 10)
            .bind("offset", offset)
            .mapTo<DeviceMapper>()
            .list()
            .map { it.toDevice() }
    }

    override fun getAllDevicesByUserId(
        userId: String,
        page: Int?,
        limit: Int?,
        deviceAlertEmail: String?,
        deviceIdChunk: String?,
        ): List<Device> {
        val offset = ((page ?: 1) - 1) * (limit ?: 10)
        return handle.createQuery(
            """
            select id, user_id, email 
            from device 
            where user_id = :user_id
            and email LIKE '%' || :email || '%'
            and id  LIKE '%' || :id || '%'
            limit :limit 
            offset :offset
        """
        )
            .bind("user_id", userId)
            .bind("email", deviceAlertEmail ?: "")
            .bind("id", deviceIdChunk ?: "")
            .bind("limit", limit ?: 10)
            .bind("offset", offset)
            .mapTo<DeviceMapper>()
            .list()
            .map { it.toDevice() }
    }

    override fun getDevicesFilteredById(id: String, userId: String, page: Int?, limit: Int?): List<Device> {
        val offset = ((page ?: 1) - 1) * (limit ?: 10)
        return handle.createQuery(
            """
            select id, user_id, email 
            from device 
            where user_id = :user_id
            and id  LIKE '%' || :id || '%'
            limit :limit 
            offset :offset
        """
        )
            .bind("user_id", userId)
            .bind("id", id)
            .bind("limit", limit ?: 10)
            .bind("offset", offset)
            .mapTo<DeviceMapper>()
            .list()
            .map { it.toDevice() }
    }
    override fun getCountOfDevicesFilteredById(userId:String, deviceId: String): Int {
        return handle.createQuery(
               """
                select count(*) 
                from device 
                where user_id = :user_id
                and id  LIKE '%' || :id || '%'
            """
        )
            .bind("user_id", userId)
            .bind("id", deviceId)
            .mapTo<Int>()
            .single()
    }
    override fun deleteDevice(deviceId: String) {
        handle.createUpdate("delete from device where id = :id")
            .bind("id", deviceId)
            .execute()
    }

    override fun getDevicesByAlertEmail(email: String): List<Device> {
        return handle.createQuery(
            """
            select id, user_id, email
            from device 
            where email = :email
            """
        )
            .bind("email", email)
            .mapTo<DeviceMapper>()
            .list()
            .map { it.toDevice() }
    }
    override fun getDeviceById(deviceId: String): Device? {
        return handle.createQuery(
            """
            select id, user_id, email 
            from device 
            where id = :id
            """
        )
            .bind("id", deviceId)
            .mapTo<DeviceMapper>()
            .singleOrNull()
            ?.toDevice()
    }
    override fun deleteAllDevices() {
        handle.createUpdate("delete from device").execute()
    }

    override fun deviceCount(userId: String, deviceAlertEmail: String?, deviceIdChunk: String?): Int {
        return handle.createQuery(
            """
            select count(*) 
            from device 
            where user_id = :user_id
            and email LIKE '%' || :email || '%'
            and id  LIKE '%' || :id || '%'
            """
        )
            .bind("user_id", userId)
            .bind("email", deviceAlertEmail ?: "")
            .bind("id", deviceIdChunk ?: "")
            .mapTo<Int>()
            .single()
    }

    override fun saveSensorErrorRecord(deviceId: String, sensorErrorRecord: SensorErrorRecord) {
        handle.createUpdate(
            """
            insert into sensor_error (device_id, sensor, timestamp)
            values (:device_id, :sensor, :timestamp)
            """
        )
            .bind("device_id", deviceId)
            .bind("sensor", sensorErrorRecord.sensorName)
            .bind("timestamp", sensorErrorRecord.instant)
            .execute()
    }

    override fun getSensorErrorRecords(deviceId: String): List<SensorErrorRecord> {
        return handle.createQuery(
            """
            select device_id, sensor, timestamp
            from sensor_error
            where device_id = :device_id
            """
        )
            .bind("device_id", deviceId)
            .mapTo<SensorErrorRecord>()
            .list()
    }

    override fun getAllSensorErrorRecords(): List<SensorErrorRecord> {
        return handle.createQuery(
            """
            select device_id, sensor, timestamp
            from sensor_error
            """
        )
            .mapTo<SensorErrorRecord>()
            .list()
    }

    override fun createDeviceLogRecord(deviceId: String, deviceWakeUpLog: DeviceWakeUpLog) {
        handle.createUpdate(
            """
            insert into device_wake_up_log (device_id, timestamp, reason)
            values (:device_id, :timestamp, :reason)
            """
        )
            .bind("device_id", deviceId)
            .bind("timestamp", deviceWakeUpLog.instant)
            .bind("reason", deviceWakeUpLog.reason)
            .execute()
    }

    override fun getDeviceLogRecords(deviceId: String): List<DeviceWakeUpLog> {
        TODO("Not yet implemented")
    }

    override fun getAllDeviceLogRecords(): List<DeviceWakeUpLog> {
        TODO("Not yet implemented")
    }
}