package pt.isel.iot_data_server.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.repository.DeviceDataRepository
import pt.isel.iot_data_server.repository.jdbi.mappers.DeviceMapper
import pt.isel.iot_data_server.repository.jdbi.mappers.UserMapper
import pt.isel.iot_data_server.repository.jdbi.mappers.toDevice
import pt.isel.iot_data_server.repository.jdbi.mappers.toUser

class JdbiDeviceDataRepository( //TODO:ORGANIZAR ISTO EM VARIOS FICHEIROS(USER,TOKEN,DEVICE...)
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
        val offset = (page ?: 0) * (limit ?: 10)
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

    override fun getAllDevices(userId: String, page: Int?, limit: Int?): List<Device> {
        val offset = ((page ?: 1) - 1) * (limit ?: 10)
        return handle.createQuery("""
            select id, user_id, email 
            from device 
            where user_id = :user_id
            limit :limit 
            offset :offset
        """)
            .bind("user_id", userId)
            .bind("limit", limit ?: 10)
            .bind("offset", offset)
            .mapTo<DeviceMapper>()
            .list()
            .map { it.toDevice() }
    }

    override fun deleteDevice(deviceId: String) {
        handle.createUpdate("delete from device where id = :id")
            .bind("id", deviceId)
            .execute()
    }
    override fun removeAllDevices() {
        handle.createUpdate("delete from device").execute()
    }

    override fun getDevicesByOwnerEmail(email: String): List<Device> {
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

    override fun deviceCount(userId: String): Int {
        return handle.createQuery(
            """
            select count(*) 
            from device 
            where user_id = :user_id
            """
        )
            .bind("user_id", userId)
            .mapTo<Int>()
            .single()
    }
}