package pt.isel.iot_data_server.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.repository.UserDataRepository
import pt.isel.iot_data_server.repository.jdbi.mappers.PasswordAndSaltMapper
import pt.isel.iot_data_server.repository.jdbi.mappers.UserMapper
import pt.isel.iot_data_server.repository.jdbi.mappers.toUser

class JdbiUserDataRepository(
    private val handle: Handle
) : UserDataRepository {
    override fun createUser(user: User) {
        handle.createUpdate(
            """
            insert into _USER (_id, email, role) values (:_id, :email, :role)
            """
        )
            .bind("_id", user.id)
            .bind("email", user.userInfo.email)
            .bind("role", user.userInfo.role)
            .execute()
    }

    override fun getAllUsers(): List<User> {
        return handle.createQuery("""
            select _id, email, role 
            from _USER
        """)
            .mapTo<UserMapper>()
            .list()
            .map { it.toUser() }
    }

    override fun getUserByToken(token: String): User? {
        return handle.createQuery(
            """
            select _id, email, role
            from _USER as users 
            inner join TOKEN as tokens on users._id = tokens.user_id
            where tokens.token = :token
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
            select _id, email, role
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
        handle.createUpdate(
            """
            insert into TOKEN (user_id, token) values (:user_id, :token)
            """
        )
            .bind("user_id", userId)
            .bind("token", token)
            .execute()
    }

    override fun getTokenFromUser(userId: String): String? {
        return handle.createQuery(
            """
            select token 
            from TOKEN
            where user_id = :user_id
            """
        )
            .bind("user_id", userId)
            .mapTo<String>()
            .singleOrNull()
    }

    override fun deleteToken(userId: String) {
        handle.createUpdate("delete from TOKEN where user_id = :user_id")
            .bind("user_id", userId)
            .execute()
    }

    override fun existsEmail(email: String): Boolean {
        getAllUsers().forEach {
            if (it.userInfo.email == email) {
                return true
            }
        }
        return false
    }

    override fun getUserByEmailOrNull(email: String): User? {
        return handle.createQuery(
            """
            select _id, email, role
            from _USER 
            where email = :email
            """
        )
            .bind("email", email)
            .mapTo<UserMapper>()
            .singleOrNull()
            ?.toUser()
    }

    override fun deleteAllTokens() {
        handle.createUpdate("delete from TOKEN").execute()
    }

    override fun deleteUser(userId: String) {
        handle.createUpdate("delete from _USER where _id = :user_id")
            .bind("user_id", userId)
            .execute()
    }

    /**
     * Used only for integration tests
     */
    override fun deleteAllUsers() {
        handle.createUpdate("delete from _USER").execute()
    }

    override fun addVerificationCode(email: String, code: String) {
        handle.createUpdate(
            """
        insert into verification_code (user_email, code) values (:user_email, :code)
        """
        )
            .bind("user_email", email)
            .bind("code", code)
            .execute()
    }

    override fun getVerificationCode(email: String): String {
        return handle.createQuery(
            """
            select code 
            from verification_code 
            where user_email = :user_email
            """
        )
            .bind("user_email", email)
            .mapTo<String>()
            .single()
    }

    override fun storePasswordAndSalt(userId: String, value: String, salt: String) {
        handle.createUpdate(
            """
            insert into password (user_id, value, salt) values (:user_id, :value, :salt)
            """
        )
            .bind("user_id", userId)
            .bind("value", value)
            .bind("salt", salt)
            .execute()
    }

    override fun getPasswordAndSalt(userId: String): Pair<String, String> {
        return handle.createQuery(
            """
            select value, salt 
            from password 
            where user_id = :user_id
            """
        )
            .bind("user_id", userId)
            .mapTo<PasswordAndSaltMapper>()
            .single()
            .let { it.value to it.salt }
    }

    override fun deleteAllPasswords() {
        handle.createUpdate("delete from password").execute()
    }
}