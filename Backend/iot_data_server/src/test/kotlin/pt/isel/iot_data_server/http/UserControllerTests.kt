package pt.isel.iot_data_server.http

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
class UserControllerTests{

    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 0

    @TestConfiguration
    class UserTestConfiguration {
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
    fun `Can create User`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        val email = "test_subject@gmail.com"
        val password = "testPassword1!"
        createUser(email, password, client)
    }

    @Test
    fun `Can get all Users as Admin`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val userEmail1 = "test_subject1@gmail.com"
        val userEmail2 = "test_subject2@gmail.com"
        val userEmail3 = "test_subject3@gmail.com"
        createUser(userEmail1, generatePassword(1), client)
        createUser(userEmail2, generatePassword(2), client)
        createUser(userEmail3, generatePassword(3), client)

        val adminToken = loginWithAdmin(client)

        val result = client.get().uri(Uris.Users.ALL)
            .header(HttpHeaders.COOKIE, "token=$adminToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>
        val users = properties["users"] as ArrayList<*>
        assertEquals(3, users.size)

        val links = result.links
        assertEquals(0, links.size)
    }

    @Test
    fun `Is email already registered`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val userEmail = "test_subject1@gmail.com"
        createUser(userEmail, generatePassword(1), client)

        val result1 = client.get().uri(Uris.Users.exists.BY_EMAIL_1.replace("{email}", userEmail))
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties1 = result1.properties as LinkedHashMap<*, *>
        val exists1 = properties1["exists"] as Boolean
        assertEquals(true, exists1)

        val anotherEmail = "test_subject2@gmail.com"
        val result2 = client.get().uri(Uris.Users.exists.BY_EMAIL_1.replace("{email}", anotherEmail))
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties2 = result2.properties as LinkedHashMap<*, *>
        val exists2 = properties2["exists"] as Boolean
        assertEquals(false, exists2)
    }

    //nao sei como testar isto
    @Test
    fun `Can get Me`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        val userToken = createUserAndLogin(email, generatePassword(1), client)

        val result = client.get().uri(Uris.Users.ME)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>
        assertEquals(3, properties.size)
        assertEquals(email, properties["email"])

        // TODO: maybe check the username and user id

        val links = result.links
        assertEquals(0, links.size)
    }

    @Test
    fun `Is logged in after log-in`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        val userToken = createUser(email, generatePassword(1), client).second

        val result = client.get().uri(Uris.NonSemantic.loggedIn)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>
        val exists = properties["isLoggedIn"] as Boolean
        assertEquals(true, exists)
    }

    @Test
    fun `Is logged in with invalid token`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val fakeToken = "fakeToken"
        val result = client.get().uri(Uris.NonSemantic.loggedIn)
            .header(HttpHeaders.COOKIE, "token=$fakeToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>
        val exists = properties["isLoggedIn"] as Boolean
        assertEquals(false, exists)
    }

    @Test
    fun `Is logged in after logout`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        val (userId, userToken) = createUser(email, generatePassword(1), client)

        client.delete().uri(Uris.NonSemantic.logout) // logs out
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isNoContent

        val result2 = client.get().uri(Uris.NonSemantic.loggedIn)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result2.properties as LinkedHashMap<*, *>
        val exists = properties["isLoggedIn"] as Boolean
        assertEquals(false, exists)
    }

    @Test
    fun `Can log-in`(){
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        val email = generateRandomEmail()
        val password = generatePassword(1)
        createUser(email, password, client)
        login(email, password, client)
    }

    @Test
    fun `Can delete User as admin`(){
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val userId1 = createUser("some_email_1@gmail.com", generatePassword(1), client).first
        val userId2 = createUser("some_email_2@gmail.com", generatePassword(2), client).first
        val userId3 = createUser("some_email_3@gmail.com", generatePassword(3), client).first

        val adminToken = loginWithAdmin(client)

        client.delete().uri(Uris.Users.BY_ID1, userId1)
            .header(HttpHeaders.COOKIE, "token=$adminToken")
            .exchange()
            .expectStatus().isNoContent

        val allUsersResult = client.get().uri(Uris.Users.ALL)
            .header(HttpHeaders.COOKIE, "token=$adminToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = allUsersResult.properties as LinkedHashMap<*, *>
        val users = properties["users"] as ArrayList<*>
        assertEquals(2, users.size)
    }

    @Test
    fun `Cannot delete User as standard user`(){
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val userToken = createUser(generateRandomEmail(), generatePassword(1), client).second
        val userId2 = createUser(generateRandomEmail(), generatePassword(1), client).first

        client.get().uri(Uris.Users.BY_ID2.replace(":id", userId2))
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
    }
}