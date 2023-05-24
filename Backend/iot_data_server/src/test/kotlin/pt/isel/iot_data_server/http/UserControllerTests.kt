package pt.isel.iot_data_server.http

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
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

/*
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTests{

    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 0

    @TestConfiguration
    class GameTestConfiguration {
        @Bean
        @Primary
        fun jdbiTest() = buildJdbiTest()
    }

    @AfterEach
    fun cleanup() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        client.delete().uri(Uris.Data.ALL)
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody
    }

    @Test
    fun `Can get all Users`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val userToken = createUserAndLogin(generateRandomEmail(), client)
        createUserAndLogin(generateRandomEmail(), client)
        createUserAndLogin(generateRandomEmail(), client)
        createUserAndLogin(generateRandomEmail(), client)

        val result = client.get().uri(Uris.Users.ALL)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val entities = result.entities
        Assertions.assertEquals(0, entities.size)

        val properties = result.properties as LinkedHashMap<*, *>
        val users = properties["users"] as ArrayList<*>
        Assertions.assertEquals(4, users.size)

        val links = result.links
        Assertions.assertEquals(0, links.size)
    }

    //nao sei como testar isto
    @Test
    fun `Can get Me`(){

        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        val userToken = createUserAndLogin(email, client)

        val result = client.get().uri(Uris.Users.ME)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>
        Assertions.assertEquals(3, properties.size)
        Assertions.assertEquals(email, properties["email"])

        // TODO: maybe check the username and user id

        val links = result.links
        Assertions.assertEquals(0, links.size)
    }
}
*/