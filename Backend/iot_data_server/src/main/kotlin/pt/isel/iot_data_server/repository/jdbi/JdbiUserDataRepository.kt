package pt.isel.iot_data_server.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.repository.UserDataRepository
import pt.isel.iot_data_server.repository.jdbi.mappers.UserMapper
import pt.isel.iot_data_server.repository.jdbi.mappers.toUser

class JdbiUserDataRepository(
    private val handle: Handle
) : UserDataRepository {
    override fun createUser(user: User) {
        handle.createUpdate(
            """
            insert into _USER (_id, username, password, email, role) values (:_id, :username, :password, :email, :role)
            """
        )
            .bind("_id", user.id)
            .bind("username", user.userInfo.username)
            .bind("password", user.userInfo.password)
            .bind("email", user.userInfo.email)
            .bind("role", user.userInfo.role)
            .execute()
    }

    override fun getAllUsers(): List<User> {
        return handle.createQuery("select _id, username, password, email, role from _USER")
            .mapTo<UserMapper>()
            .list()
            .map { it.toUser() }
    }

    override fun getUserByToken(token: String): User? {
        return handle.createQuery(
            """
            select _id, username, password, email, role 
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
            select _id, username, password, email, role
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

    override fun existsUsername(username: String): Boolean {
        return handle.createQuery("""
            select username 
            from _USER 
            where username = :username
        """)
            .bind("username", username)
            .mapTo<String>()
            .list()
            .isNotEmpty()
    }

    override fun getUserByUsernameOrNull(username: String): User? {
        return handle.createQuery(
            """
            select _id, username, password, email, role
            from _USER as users 
            where username = :username
            """
        )
            .bind("username", username)
            .mapTo<UserMapper>()
            .singleOrNull()
            ?.toUser()
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
            select _id, username, password, email, role
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
            where email = :email
            """
        )
            .bind("email", email)
            .mapTo<String>()
            .single()
    }
}