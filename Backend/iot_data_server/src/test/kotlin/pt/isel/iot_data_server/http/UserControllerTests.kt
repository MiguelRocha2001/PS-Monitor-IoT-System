package pt.isel.iot_data_server.http

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.iot_data_server.http.controllers.Uris
import pt.isel.iot_data_server.http.infra.SirenModel
import pt.isel.iot_data_server.repository.jdbi.configure
import pt.isel.iot_data_server.utils.generateRandomEmail
import pt.isel.iot_data_server.utils.generateRandomName
import pt.isel.iot_data_server.utils.generateRandomPassword


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTests {

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

    private fun create_user(username:String, password:String, email:String, client: WebTestClient){
        val result = client.post().uri(Uris.Users.ALL)
            .bodyValue(mapOf(
                "username" to username,
                "password" to password,
                "email" to email,
            ))
            .exchange()
            .expectStatus().isCreated
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `Can get all Users`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        create_user( generateRandomName(),generateRandomPassword(),generateRandomEmail(), client)
        create_user( generateRandomName(),generateRandomPassword(),generateRandomEmail(), client)
        create_user( generateRandomName(),generateRandomPassword(),generateRandomEmail(), client)
        create_user( generateRandomName(),generateRandomPassword(),generateRandomEmail(), client)

      /*  create_user(email, client)
        create_user(email, client)
        create_user(email, client)*/

        val result = client.get().uri(Uris.Users.ALL)
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val entities = result.entities
        Assertions.assertEquals(0, entities.size)

        val properties = result.properties as LinkedHashMap<*, *>
        val users = properties["users"] as ArrayList<*>
        Assertions.assertEquals(3, users.size)

        val links = result.links
        Assertions.assertEquals(0, links.size)
    }

    //nao sei como testar isto
    @Test
    fun `Can get Me`(){

        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        create_user( generateRandomName(),generateRandomPassword(),email, client)

        val result = client.get().uri(Uris.Users.ME)
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>
        Assertions.assertEquals(1, properties.size)
        Assertions.assertEquals(email, properties["email"])

        val links = result.links
        Assertions.assertEquals(0, links.size)
    }




}
