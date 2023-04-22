package pt.isel.iot_data_server.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.StaticDataRepository
import pt.isel.iot_data_server.repository.jdbi.mappers.DeviceMapper
import pt.isel.iot_data_server.repository.jdbi.mappers.UserMapper
import pt.isel.iot_data_server.repository.jdbi.mappers.toDevice
import pt.isel.iot_data_server.repository.jdbi.mappers.toUser

class JdbiServerRepository( //TODO:ORGANIZAR ISTO EM VARIOS FICHEIROS(USER,TOKEN,DEVICE...)
    private val handle: Handle
) : StaticDataRepository {
    override fun createUser(user: User) {
        handle.createUpdate(
            """
            insert into _USER (_id, username, password, email) values (:_id, :username, :password, :email)
            """
        )
            .bind("_id", user.id)
            .bind("username", user.userInfo.username)
            .bind("password", user.userInfo.password)
            .bind("email", user.userInfo.email)
            .execute()
    }

    override fun getAllUsers(): List<User> {
        return handle.createQuery("select _id, username, password, email from _USER")
            .mapTo<UserMapper>()
            .list()
            .map { it.toUser() }
    }

    override fun getUserByToken(token: String): User? {
        return handle.createQuery(
            """
            select _id, username, password, email 
            from _USER as users 
            inner join TOKEN as tokens
            on users._id = tokens.user_id
            where token = :token
            """
        )
            .bind("token", token)
            .mapTo<UserMapper>()
            .singleOrNull()
            ?.toUser()
    }

    override fun getUserByIdOrNull(userId: String): User? {
        return handle.createQuery(
            """
            select _id, username, password, email 
            from _USER as users 
            where _id = :user_id
            """
        )
            .bind("user_id", userId)
            .mapTo<UserMapper>()
            .singleOrNull()
            ?.toUser()
    }

    override fun createToken(userId: String, token: String) {
        handle.createUpdate("delete from TOKEN where user_id = :user_id")
            .bind("user_id", userId)
            .execute()

        handle.createUpdate("insert into TOKEN(user_id, token) values (:user_id, :token)")
            .bind("user_id", userId)
            .bind("token", token)
            .execute()
    }

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

    override fun getAllDevices(): List<Device> {
        return handle.createQuery("select id, email from device")
            .mapTo<DeviceMapper>()
            .list()
            .map { it.toDevice() }
    }

    override fun getAllDevices(userId: String): List<Device> {
        return handle.createQuery("""
            select id, user_id, email 
            from device 
            where user_id = :user_id
        """)
            .bind("user_id", userId)
            .mapTo<DeviceMapper>()
            .list()
            .map { it.toDevice() }
    }

    override fun deleteDevice(deviceId: String) {
        handle.createUpdate("delete from device where id = :id")
            .bind("id", deviceId)
            .execute()
    }

    override fun existsUsername(username: String): Boolean {
        return handle.createQuery("select username from _USER")
            .mapTo<String>()
            .list().size > 0
    }

    override fun getUserByUsernameOrNull(username: String): User? {
        return handle.createQuery("select _id, username, password, email from _USER")
            .mapTo<UserMapper>()
            .list()
            .map { it.toUser() }
            .firstOrNull { it.userInfo.username == username }
    }

    override fun existsEmail(email: String): Boolean {
        getAllUsers().forEach {
            if (it.userInfo.email == email) {
                return true
            }
        }
        return false
    }

    override fun saveSalt(userId: String, salt: String) {
        handle.createUpdate(
            """
            insert into salt (user_id, salt) values (:user_id, :salt)
            """
        )
            .bind("user_id", userId)
            .bind("salt", salt)
            .execute()
    }

    override fun getSalt(userId: String): String {
        return handle.createQuery(
            """
        SELECT salt 
        FROM salt 
        WHERE user_id = :user_id
        """
        )
            .bind("user_id", userId)
            .mapTo<String>() // Retrieve the salt as a String
            .single()
    }

    override fun getUserByEmailAddressOrNull(email: String): User? {
        return handle.createQuery(
            """
            select _id, username, password, email 
            from _USER 
            where email = :email
            """
        )
            .bind("email", email)
            .mapTo<UserMapper>()
            .singleOrNull()
            ?.toUser()
    }

    override fun removeAllDevices() {
        handle.createUpdate("delete from device").execute()
    }

    override fun getDevicesByOwnerEmail(email: String): List<Device> {
        return handle.createQuery(
            """
            select id, email 
            from device 
            where email = :email
            """
        )
            .bind("email", email)
            .mapTo<DeviceMapper>()
            .list()
            .map { it.toDevice() }
    }

    /**
     * Used only for integration tests
     */
    override fun deleteAllUsers() {
        handle.createUpdate("delete from _USER").execute()
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
}