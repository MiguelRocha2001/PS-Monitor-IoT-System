package pt.isel.iot_data_server.http

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions
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
import pt.isel.iot_data_server.http.controllers.Uris
import pt.isel.iot_data_server.http.infra.SirenModel
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.repository.jdbi.JdbiTransaction
import pt.isel.iot_data_server.repository.jdbi.configure
import pt.isel.iot_data_server.utils.generateRandomEmail
import pt.isel.iot_data_server.utils.generateRandomName
import pt.isel.iot_data_server.utils.generateRandomPassword
import javax.sql.DataSource


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTests{

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
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        client.delete().uri(Uris.Users.ALL)
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody
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
