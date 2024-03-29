package pt.isel.iot_data_server.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceWakeUpLog
import pt.isel.iot_data_server.domain.SensorErrorRecord
import pt.isel.iot_data_server.repository.DeviceDataRepository
import pt.isel.iot_data_server.repository.jdbi.mappers.DeviceMapper
import pt.isel.iot_data_server.repository.jdbi.mappers.DeviceWakeUpLogMapper
import pt.isel.iot_data_server.repository.jdbi.mappers.toDevice
import pt.isel.iot_data_server.repository.jdbi.mappers.toDeviceWakeUpLog
import java.sql.Timestamp

class JdbiDeviceDataRepository(
    private val handle: Handle
) : DeviceDataRepository {
    override fun createDevice(userId: String, device: Device) {
        handle.createUpdate(
            """
            insert into device (id, user_id, email, created_at) values (:id, :user_id, :email, :created_at)
            """
        )
            .bind("id", device.deviceId)
            .bind("user_id", userId)
            .bind("email", device.ownerEmail)
            .bind("created_at", device.createdAt)
            .execute()
    }

    override fun getAllDevices(page: Int?, limit: Int?): List<Device> {
        val offset = ((page ?: 1) - 1) * (limit ?: 10)
        return handle.createQuery("""
            select id, user_id, email, created_at
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
            select id, user_id, email, created_at
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
            select id, user_id, email, created_at
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
            select id, user_id, email, created_at
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
            select id, user_id, email, created_at
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
            .bind("sensor", sensorErrorRecord.sensorType)
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

    override fun createDeviceWakeUpLogs(deviceId: String, deviceWakeUpLog: DeviceWakeUpLog) {
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

    override fun getDeviceWakeUpLogByDeviceId(deviceId: String, timestamp: Timestamp): DeviceWakeUpLog? {
        return handle.createQuery(
            """
            select device_id, timestamp, reason
            from device_wake_up_log
            where device_id = :device_id
            and timestamp = :timestamp
            """
        )
            .bind("device_id", deviceId)
            .bind("timestamp", timestamp)
            .mapTo<DeviceWakeUpLogMapper>()
            .singleOrNull()
            ?.toDeviceWakeUpLog()
    }

    override fun getDeviceWakeUpLogs(deviceId: String): List<DeviceWakeUpLog> {
        return handle.createQuery(
            """
            select device_id, timestamp, reason
            from device_wake_up_log
            where device_id = :device_id
            """
        )
            .bind("device_id", deviceId)
            .mapTo<DeviceWakeUpLogMapper>()
            .list()
            .map { it.toDeviceWakeUpLog() }
    }

    override fun updateDeviceWakeUpLogs(deviceId: String, deviceWakeUpLog: DeviceWakeUpLog) {
        handle.createUpdate(
            """
            update device_wake_up_log
            set reason = :reason
            where device_id = :device_id
            and timestamp = :timestamp
            """
        )
            .bind("device_id", deviceId)
            .bind("timestamp", deviceWakeUpLog.instant)
            .bind("reason", deviceWakeUpLog.reason)
            .execute()
    }
}