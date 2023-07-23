package pt.isel.iot_data_server.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.repository.UserDataRepository
import pt.isel.iot_data_server.repository.jdbi.mappers.PasswordAndSaltMapper
import pt.isel.iot_data_server.repository.jdbi.mappers.UserMapper
import pt.isel.iot_data_server.repository.jdbi.mappers.toUser
import pt.isel.iot_data_server.service.user.Role
import com.nimbusds.oauth2.sdk.device.UserCode

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
            .bind("role", user.userInfo.role.name.lowercase())
            .execute()
    }

    private enum class UserColumn() {
        ID, ALL
    }
    private sealed class UsersQuery
    private data class AllUserIdsQuery(val userIds: List<String>) : UsersQuery()
    private data class AllUsersQuery(val users: List<User>) : UsersQuery()
    private fun userQuery(userColumn: UserColumn, role: Role?, page: Int?, limit: Int?, email: String?, userId: String?): UsersQuery {
        // chooses the query to execute based on the userColumn
        val query = if (userColumn == UserColumn.ID) {
            StringBuilder("""
                select _id
                from _USER
            """)
        } else {
            StringBuilder("""
                select _id, email, role
                from _USER
            """)
        }

        if (role != null) {
            query.append(" where role = :role")
        }

        if (email != null) {
            if (role != null)
                query.append(" and email = :email")
            else
                query.append(" where email = :email")
        }

        if (userId != null) {
            if (role != null || email != null)
                query.append(" and _id like :_id")
            else
                query.append(" where _id like :_id")
        }

        if (page != null && limit != null) {
            query.append(" limit :limit offset :offset")
        }

        val t = handle.createQuery(query.toString())
            .apply {
                if (role != null) {
                    bind("role", role.name.lowercase())
                }

                if (email != null) {
                    bind("email", email)
                }

                if (userId != null) {
                    bind("_id", "%$userId%")
                }

                if (page != null && limit != null) {
                    bind("limit", limit)
                    bind("offset", (page - 1) * limit)
                }
            }
            .mapTo<UserMapper>()
            .list()

        if (userColumn == UserColumn.ID)
            return AllUserIdsQuery(t.map { it._id })
        else
            return AllUsersQuery(t.map { it.toUser() })
    }

    override fun getAllUsers(role: Role?, page: Int?, limit: Int?, email: String?, userId: String?): List<User> {
        return (userQuery(UserColumn.ALL, role, page, limit, email, userId) as AllUsersQuery).users
    }

    override fun getAllUserIds(role: Role?, page: Int?, limit: Int?, email: String?, userId: String?): List<String> {
        return (userQuery(UserColumn.ID, role, page, limit, email, userId) as AllUserIdsQuery).userIds
    }

    override fun getUserCount(): Int {
        return handle.createQuery("""
            select count(_id) 
            from _USER
        """)
            .mapTo<Int>()
            .single()
    }

    override fun getAllUsersWithRole(role: Role): List<User> {
        return handle.createQuery("""
            select _id, email, role 
            from _USER
            where role = :role
        """)
            .bind("role", role.name.lowercase())
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

    override fun deleteUserToken(userId: String) {
        handle.createUpdate(
            """
            delete from TOKEN where user_id = :user_id
            """
        )
            .bind("user_id", userId)
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

    // TODO: optimize this query
    override fun existsEmail(email: String): Boolean {
        return handle.createQuery(
            """
            select count(*) 
            from _USER 
            where email = :email
            """
        )
            .bind("email", email)
            .mapTo<Int>()
            .single() > 0
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

    override fun getUsersByEmailChunkOrNull(emailChunk: String): List<User> {
        return handle.createQuery(
            """
            select _id, email, role
            from _USER 
            where email like :emailChunk
            """
        )
            .bind("emailChunk", "%$emailChunk%")
            .mapTo<UserMapper>()
            .list()
            .map { it.toUser() }
    }

    override fun getUserCountByEmailChunkOrNull(emailChunk: String): Int {
        return handle.createQuery(
            """
            select count(*) 
            from _USER 
            where email like :emailChunk
            """
        )
            .bind("emailChunk", "%$emailChunk%")
            .mapTo<Int>()
            .single()
    }

    override fun deleteAllTokens(role: Role?) {
        if (role == null) {
            handle.createUpdate("delete from TOKEN").execute()
        } else {
            handle.createUpdate(
                """
                delete from TOKEN 
                where user_id in (
                    select _id 
                    from _USER 
                    where role = :role
                )
                """
            )
                .bind("role", role.name.lowercase())
                .execute()
        }
    }

    override fun deleteUser(userId: String) {
        handle.createUpdate("delete from _USER where _id = :user_id")
            .bind("user_id", userId)
            .execute()
    }

    /**
     * Used only for integration tests
     */
    override fun deleteAllUsers(role: Role?) {
        if (role == null) {
            handle.createUpdate("delete from _USER").execute()
        } else {
            handle.createUpdate("delete from _USER where role = :role")
                .bind("role", role.name.lowercase())
                .execute()
        }
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

    override fun hasPassword(userId: String): Boolean {
        return handle.createQuery(
            """
            select count(*) 
            from password 
            where user_id = :user_id
            """
        )
            .bind("user_id", userId)
            .mapTo<Int>()
            .single() > 0
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

    override fun deleteAllPasswords(role: Role?) {
        if (role == null) {
            handle.createUpdate("delete from password").execute()
        } else {
            handle.createUpdate(
                """
                delete from password 
                where user_id in (
                    select _id 
                    from _USER 
                    where role = :role
                )
                """
            )
                .bind("role", role.name.lowercase())
                .execute()
        }
    }

    override fun deleteUserPassword(userId: String) {
        handle.createUpdate("delete from password where user_id = :user_id")
            .bind("user_id", userId)
            .execute()
    }
}