package pt.isel.iot_data_server.http

import org.junit.jupiter.api.Assertions
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.iot_data_server.http.controllers.Uris
import pt.isel.iot_data_server.http.infra.SirenModel

fun String.toApiUri(): String {
    return Uris.API + this
}

/**
 * Creates a user and returns the token
 */
@Deprecated("Use createUser instead")
fun createUserAndLogin(email: String, password: String, client: WebTestClient): String {
    createUser(email, password, client)
    return login(email, password, client)
}

/**
 * Creates a User.
 * @return the token and user id.
 */
fun createUser(email: String, password: String, client: WebTestClient): Pair<String, String> {
    val result = client.post().uri(Uris.Users.ALL.toApiUri())
        .bodyValue(
            mapOf(
                "email" to email,
                "password" to password
            )
        )
        .exchange()
        .expectStatus().isCreated
        .expectBody(SirenModel::class.java)
        .returnResult()
        .responseBody

    val userId = (result?.properties as java.util.LinkedHashMap<String, String>)["userId"] ?: Assertions.fail("No user Id")
    val token = (result.properties as java.util.LinkedHashMap<String, String>)["token"] ?: Assertions.fail("No token")
    return Pair(userId, token)
}

/**
 * Creates a user and returns the token
 */
fun login(email: String, password: String, client: WebTestClient): String {
    val result = client.post().uri(Uris.Users.MY_TOKEN.toApiUri())
        .bodyValue(
            mapOf(
                "email" to email,
                "password" to password,
            )
        )
        .exchange()
        .expectStatus().isCreated // creates a new token, in server
        .expectBody(SirenModel::class.java)
        .returnResult()
        .responseBody

    // extracts the token from response
    return (result?.properties as java.util.LinkedHashMap<String, String>)["token"] ?: Assertions.fail("No token")
}

fun loginWithAdmin(client: WebTestClient) =
    login("admin_email@gmail.com", "admin-password", client) // logs with admin

internal fun create_device(email: String, client: WebTestClient, userToken: String): String {
    val result = client.post().uri(Uris.Users.Devices.ALL.toApiUri())
        .header(HttpHeaders.COOKIE, "token=$userToken")
        .bodyValue(mapOf("email" to email))
        .exchange()
        .expectStatus().isCreated
        .expectBody(SirenModel::class.java)
        .returnResult()
        .responseBody!!

    val properties = result.properties as LinkedHashMap<*, *>
    Assertions.assertEquals(1, properties.size)
    Assertions.assertEquals(6, (properties["deviceId"] as? String)?.length)

    return properties["deviceId"] as String
}

fun deleteAllStandardUsers(client: WebTestClient) {
    val adminToken = loginWithAdmin(client)
    client.delete().uri(Uris.Users.ALL.toApiUri())
        .header(HttpHeaders.COOKIE, "token=$adminToken")
        .exchange()
        .expectStatus().isOk
        .expectBody(SirenModel::class.java)
        .returnResult()
        .responseBody

    val allUsersResult = client.get().uri(Uris.Users.ALL)
        .header(HttpHeaders.COOKIE, "token=$adminToken")
        .exchange()
        .expectStatus().isOk
        .expectBody(SirenModel::class.java)
        .returnResult()
        .responseBody!!

    val properties = allUsersResult.properties as LinkedHashMap<*, *>
    val users = properties["users"] as ArrayList<*>
    Assertions.assertEquals(0, users.size)
}

fun eraseAllData(client: WebTestClient) {
    client.delete().uri((Uris.Data.ALL + "?leave-admin-user=true").toApiUri())
        .exchange()
        .expectStatus().isOk
        .expectBody(SirenModel::class.java)
        .returnResult()
        .responseBody
}