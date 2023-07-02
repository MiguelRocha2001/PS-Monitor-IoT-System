package pt.isel.iot_data_server.http

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
        val (userId, userToken) = createUser(email, generatePassword(1), client)

        create_device(generateRandomEmail(), client, userToken)
        create_device(generateRandomEmail(), client, userToken)
        create_device(generateRandomEmail(), client, userToken)

        val result = client.get().uri(Uris.Users.Devices.ALL_1.toApiUri(), userId)
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
    fun `Can get Device Count`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        // creates random user, and logs in (results in a valid token inside a cookie)
        val (userId, userToken) = createUser(email, generatePassword(1), client)

        create_device(generateRandomEmail(), client, userToken)
        create_device(generateRandomEmail(), client, userToken)
        create_device(generateRandomEmail(), client, userToken)

        val result = client.get().uri(Uris.Users.Devices.COUNT_1.toApiUri(), userId)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>
        val deviceCount = properties["deviceCount"] as Int
        assertEquals(3, deviceCount)
    }

    @Test
    fun `Get devices filtered bi id`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        // creates random user, and logs in (results in a valid token inside a cookie)
        val (_, userToken) = createUser(email, generatePassword(1), client)

        val deviceId = create_device(generateRandomEmail(), client, userToken)
        create_device(generateRandomEmail(), client, userToken)
        create_device(generateRandomEmail(), client, userToken)

        val result = client.get().uri((Uris.Users.Devices.ALL_2 + "?id=${deviceId[2]}").toApiUri()) // second char of the id
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>
        val devices = properties["devices"] as ArrayList<*>
        assertTrue(devices.any { (it as LinkedHashMap<*, *>)["id"] == deviceId })
    }

    @Test
    fun `Count devices filtered bi id`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        // creates random user, and logs in (results in a valid token inside a cookie)
        val (_, userToken) = createUser(email, generatePassword(1), client)

        val deviceId = create_device(generateRandomEmail(), client, userToken)
        create_device(generateRandomEmail(), client, userToken)
        create_device(generateRandomEmail(), client, userToken)

        val result = client.get().uri((Uris.Users.Devices.COUNT_2 + "?id=${deviceId[2]}").toApiUri()) // second char of the id
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>
        val devices = properties["deviceCount"] as Int
        assertEquals(1, devices)
    }

    @Test
    fun `Create and get device by id`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        val (_, userToken) = createUser(email, generatePassword(1), client)
        val deviceId = create_device(email, client, userToken)

        val result = client.get().uri(Uris.Users.Devices.BY_ID1.toApiUri(), deviceId)
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
        val (userId, userToken) = createUser(email, generatePassword(1), client)

        create_device(email, client, userToken)

        client.get().uri(Uris.Users.Devices.BY_ID1.toApiUri(),"invalid_id")
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `Get all devices by email`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        // creates random user, and logs in (results in a valid token inside a cookie)
        val (userId, userToken) = createUser(email, generatePassword(1), client)

        create_device(email, client, userToken)
        create_device(email, client, userToken)
        create_device(email, client, userToken)


        val result = client.get().uri((Uris.Users.Devices.ALL_1 + "?email=$email").toApiUri(), userId)
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
    fun `Get Device wake up logs`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        // creates random user, and logs in (results in a valid token inside a cookie)
        val (_, userToken) = createUser(email, generatePassword(1), client)
        val deviceId = create_device(email, client, userToken)

        val result = client.get().uri(Uris.Users.Devices.WakeUpLogs.ALL_1.toApiUri(), deviceId)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>
        val logs = properties["logs"] as ArrayList<*>
        assertEquals(0, logs.size)
    }
}