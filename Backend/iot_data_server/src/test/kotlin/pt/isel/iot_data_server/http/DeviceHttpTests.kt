package pt.isel.iot_data_server.http

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.iot_data_server.http.controllers.Uris
import pt.isel.iot_data_server.http.infra.SirenModel
import pt.isel.iot_data_server.utils.generateRandomEmail


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceHttpTests {

    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 0

    @TestConfiguration
    class DeviceTestConfiguration {
        @Bean
        @Primary
        fun jdbiTest() = buildJdbiTest()
    }

    @BeforeEach
    fun cleanup() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        eraseAllData(client)
    }

    @Test
    fun `Can get all Devices`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        // creates random user, and logs in (results in a valid token inside a cookie)
        val userToken = createUserAndLogin(email, generatePassword(1), client)

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
    fun `Create and get device by id`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        val userToken = createUserAndLogin(email, generatePassword(1), client)
        val deviceId = create_device(email, client, userToken)

        val result = client.get().uri(Uris.Devices.BY_ID1, deviceId)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>

        assertEquals(deviceId, properties["id"])
        assertEquals(email, properties["email"])
    }

    @Test
    fun `get invalid device by id`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        val userToken = createUserAndLogin(email, generatePassword(1), client)

        create_device(email, client, userToken)

        client.get().uri(Uris.Devices.BY_ID1,"invalid_id")
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `get all devices`(){
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        // creates random user, and logs in (results in a valid token inside a cookie)
        val userToken = createUserAndLogin(email, generatePassword(1), client)

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
        assertEquals(3, devices.size)
    }

    @Test
    fun `get all devices by email`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        // creates random user, and logs in (results in a valid token inside a cookie)
        val userToken = createUserAndLogin(email, generatePassword(1), client)

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