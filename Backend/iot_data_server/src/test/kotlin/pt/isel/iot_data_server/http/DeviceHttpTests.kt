package pt.isel.iot_data_server.http

import deleteAllDeviceRecords
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.http.controllers.Uris
import pt.isel.iot_data_server.http.infra.SirenModel
import pt.isel.iot_data_server.repository.jdbi.configure
import pt.isel.iot_data_server.utils.generateRandomEmail


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceHttpTests {

    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 0

    @TestConfiguration
    class GameTestConfiguration {
        @Bean
        @Primary
        fun jdbiTest() = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(System.getenv("DB_POSTGRES_IOT_SYSTEM_TEST"))
            }
        ).configure()
    }

    @BeforeEach
    fun setup() {
        deleteAllDeviceRecords()
    }


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

    fun create_device(email: String, client: WebTestClient, userToken: String): DeviceId {
        val result = client.post().uri(Uris.Devices.ALL)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .bodyValue(mapOf("email" to email))
            .exchange()
            .expectStatus().isCreated
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>
        assertEquals(1, properties.size)
        assertEquals(8, (properties["deviceId"] as? String)?.length)

        // asserting links
        val links = result.links
        assertEquals(0, links.size)

        // asserting actions
        val actions = result.actions
        assertEquals(0, actions.size)

        return DeviceId(properties["deviceId"] as String)
    }


    @Test
    fun `Can get all Devices`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        // creates random user, and logs in (results in a valid token inside a cookie)
        val userToken = createUserAndLogin(email, client)

        create_device(generateRandomEmail(), client, userToken)
        create_device(generateRandomEmail(), client, userToken)
        create_device(generateRandomEmail(), client, userToken)

        val result = client.get().uri(Uris.Devices.ALL)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>
        val devices = properties["devices"] as ArrayList<*>
        assertEquals(3,devices.size)

        val links = result.links
        assertEquals(0, links.size)

        // asserting actions
        val actions = result.actions
        assertEquals(0, actions.size)
    }

    @Test
    fun `get device by id`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        val userToken = createUserAndLogin(email, client)

        val deviceId = create_device(email, client, userToken)
        val result = client.get().uri(Uris.Devices.BY_ID1,deviceId.id)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>

        assertEquals(deviceId.id,properties["id"])
        assertEquals(email,properties["email"])
    }

    @Test
    fun `get invalid device by id`(){ //TODO: change to 404
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        val userToken = createUserAndLogin(email, client)

        create_device(email, client, userToken)
        client.get().uri(Uris.Devices.BY_ID1,"INVALID_ID")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `get all devices`(){
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        // creates random user, and logs in (results in a valid token inside a cookie)
        val userToken = createUserAndLogin(email, client)

        create_device(email, client, userToken)
        create_device(email, client, userToken)
        create_device(email, client, userToken)

        val result = client.get().uri(Uris.Devices.ALL)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>
        val devices = properties["devices"] as ArrayList<*>
        assertEquals(3,devices.size)
    }

    @Test
    fun `get all devices by email`(){
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        // creates random user, and logs in (results in a valid token inside a cookie)
        val userToken = createUserAndLogin(email, client)

        create_device(email, client, userToken)
        create_device(email, client, userToken)
        create_device(email, client, userToken)

        val result = client.get().uri(Uris.Devices.BY_EMAIL, email)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>
        val devices = properties["devices"] as ArrayList<*>
        assertEquals(3,devices.size)
    }
}