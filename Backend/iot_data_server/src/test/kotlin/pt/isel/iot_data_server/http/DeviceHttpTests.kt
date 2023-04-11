package pt.isel.iot_data_server.http

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertEquals
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


    fun create_device(email:String) {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val result = client.post().uri(Uris.Devices.ALL)
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
    }

    @Test
    fun `Can get all Devices`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        create_device(generateRandomEmail())
        create_device(generateRandomEmail())
        create_device(generateRandomEmail())
        val result = client.get().uri(Uris.Devices.ALL)
            .exchange()
            .expectStatus().isCreated
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!
        val properties = result.properties as LinkedHashMap<*, *>
        val devices = properties["devices"] as ArrayList<*>
        assertEquals(3,devices.size == 3)
    }


}