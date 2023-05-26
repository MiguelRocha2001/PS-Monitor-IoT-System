package pt.isel.iot_data_server.http

import org.junit.jupiter.api.Assertions
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.iot_data_server.http.controllers.Uris
import pt.isel.iot_data_server.http.infra.SirenModel

/**
 * Creates a user and returns the token
 */
fun createUserAndLogin(email: String, password: String, client: WebTestClient): String {
    createUser(email, password, client)
    return login(email, password, client)
}

/**
 * Creates a User.
 * @return the token and user id.
 */
fun createUser(email: String, password: String, client: WebTestClient): Pair<String, String> {
    val result = client.post().uri(Uris.Users.ALL)
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
    return Pair(token, userId)
}

/**
 * Creates a user and returns the token
 */
fun login(email: String, password: String, client: WebTestClient): String {
    val result = client.post().uri(Uris.Users.MY_TOKEN)
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

fun create_device(email: String, client: WebTestClient, userToken: String): String {
    val result = client.post().uri(Uris.Devices.ALL)
        .header(HttpHeaders.COOKIE, "token=$userToken")
        .bodyValue(mapOf("email" to email))
        .exchange()
        .expectStatus().isCreated
        .expectBody(SirenModel::class.java)
        .returnResult()
        .responseBody!!

    val properties = result.properties as LinkedHashMap<*, *>
    Assertions.assertEquals(1, properties.size)
    Assertions.assertEquals(8, (properties["deviceId"] as? String)?.length)

    // asserting links
    val links = result.links
    Assertions.assertEquals(0, links.size)

    // asserting actions
    val actions = result.actions
    Assertions.assertEquals(0, actions.size)

    return properties["deviceId"] as String
}