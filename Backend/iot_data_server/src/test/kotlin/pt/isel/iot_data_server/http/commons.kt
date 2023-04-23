package pt.isel.iot_data_server.http

import org.junit.jupiter.api.Assertions
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.iot_data_server.http.controllers.Uris
import pt.isel.iot_data_server.http.infra.SirenModel

/**
 * Creates a user and returns the token
 */
fun createUserAndLogin(email: String, client: WebTestClient): String {
    val username = email.split("@")[0]
    val password = "Static=password1"

    client.post().uri(Uris.Users.ALL)
        .bodyValue(
            mapOf(
                "username" to username,
                "password" to password,
                "email" to email
            )
        )
        .exchange()
        .expectStatus().isCreated
        .expectBody(SirenModel::class.java)
        .returnResult()
        .responseBody

    val result = client.post().uri(Uris.Users.MY_TOKEN)
        .bodyValue(
            mapOf(
                "username" to username,
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