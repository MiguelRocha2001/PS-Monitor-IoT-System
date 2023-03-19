package pt.isel.iot_data_server.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.ServerRepository
import pt.isel.iot_data_server.repository.jdbi.mappers.DeviceMapper
import pt.isel.iot_data_server.repository.jdbi.mappers.UserMapper
import pt.isel.iot_data_server.repository.jdbi.mappers.toDevice
import pt.isel.iot_data_server.repository.jdbi.mappers.toUser

class JdbiServerRepository(
    private val handle: Handle
) : ServerRepository {
    override fun createUser(user: User) {
        handle.createUpdate(
            """
            insert into _USER (username, password) values (:username, :password_validation)
            """
        )
            .bind("id", user.id)
            .bind("username", user.username)
            .bind("password_validation", user.password)
            .execute()
    }

    override fun getAllUsers(): List<User> {
        return handle.createQuery("select id, username from _USER")
            .mapTo<UserMapper>()
            .list()
            .map { it.toUser() }
    }

    override fun getUserByToken(token: String): User? {
        return handle.createQuery(
            """
            select id, username 
            from _USER as users 
            inner join TOKEN as tokens
            on users.id = tokens.user_id
            where token = :token
            """
        )
            .bind("token", token)
            .mapTo<UserMapper>()
            .singleOrNull()
            ?.toUser()
    }

    override fun addToken(userId: Int, token: String) {
        handle.createUpdate("delete from TOKEN where user_id = :user_id")
            .bind("user_id", userId)
            .execute()

        handle.createUpdate("insert into TOKEN(user_id, token) values (:user_id, :token)")
            .bind("user_id", userId)
            .bind("token", token)
            .execute()
    }

    override fun addDevice(device: Device) {
        handle.createUpdate(
            """
            insert into device (id) values (:id)
            """
        )
            .bind("id", device.deviceId.id)
            .execute()
    }

    override fun getAllDevices(): List<Device> {
        return handle.createQuery("select id from device")
            .mapTo<DeviceMapper>()
            .list()
            .map { it.toDevice() }
    }

    override fun savePhRecord(deviceId: DeviceId, phRecord: PhRecord) {
        handle.createUpdate(
            """
            insert into ph_record (device_id, time, value) values (:device_id, :time, :value)
            """
        )
            .bind("device_id", deviceId)
            .bind("time", phRecord.timestamp)
            .bind("value", phRecord.value)
    }

    override fun getPhRecords(deviceId: DeviceId): List<PhRecord> {
        return handle.createQuery(
            """
            select time, value 
            from ph_record 
            where device_id = :device_id
            """
        )
            .bind("device_id", deviceId)
            .mapTo<PhRecord>()
            .list()
    }

    override fun saveTemperatureRecord(deviceId: DeviceId, temperatureRecord: TemperatureRecord) {
        handle.createUpdate(
            """
            insert into temperature_record (device_id, time, value) values (:device_id, :time, :value)
            """
        )
            .bind("device_id", deviceId)
            .bind("time", temperatureRecord.timestamp)
            .bind("value", temperatureRecord.value)
    }

    //  TODO - Optimize using the power of relational database queries
    override fun exists(user: User): Boolean {
        getAllUsers().forEach {
            if (it.username == user.username) {
                return true
            }
        }
        return false
    }

    //  TODO - Optimize using the power of relational database queries
    override fun getUserByUsername(username: String): User {
        getAllUsers().forEach {
            if (it.username == username) {
                return it
            }
        }
        throw Exception("User not found")
    }

    override fun getTemperatureRecords(deviceId: DeviceId): List<TemperatureRecord> {
        return handle.createQuery(
            """
            select time, value 
            from temperature_record 
            where device_id = :device_id
            """
        )
            .bind("device_id", deviceId)
            .mapTo<TemperatureRecord>()
            .list()
    }
}