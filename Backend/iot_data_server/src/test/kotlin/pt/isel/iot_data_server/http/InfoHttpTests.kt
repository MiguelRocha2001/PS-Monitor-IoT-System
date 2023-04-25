package pt.isel.iot_data_server.http

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpMethod
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.iot_data_server.http.controllers.Rels
import pt.isel.iot_data_server.http.controllers.Uris
import pt.isel.iot_data_server.http.infra.SirenModel
import java.net.URI


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InfoHttpTests {

    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 0

    @TestConfiguration
    class GameTestConfiguration {
        @Bean
        @Primary
        fun jdbiTest() = buildJdbiTest()
    }

    @Test
    fun `Can obtain a new Device Id`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val result = client.get().uri("/siren-info")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!

        val properties = result.properties as LinkedHashMap<*, *>
        assertEquals(0, (properties.size))

        assertEquals(result.clazz, listOf("siren-info"))

        // asserting links
        val links = result.links
        assertEquals(7, links.size)
        links.any { it.rel.contains(Rels.IS_LOGGED_IN.value) && it.href == Uris.NonSemantic.loggedIn().toASCIIString() }
        links.any { it.rel.contains(Rels.ME.value) && it.href == URI(Uris.Users.ME).toASCIIString() }
        links.any { it.rel.contains(Rels.DEVICES.value) && it.href == Uris.Devices.all().toASCIIString() }
        links.any { it.rel.contains(Rels.DEVICE_BY_ID.value) && it.href == Uris.Devices.byId().toASCIIString() }
        links.any { it.rel.contains(Rels.PH_DATA.value) && it.href == Uris.Devices.PH.all().toASCIIString() }
        links.any { it.rel.contains(Rels.TEMPERATURE_DATA.value) && it.href == Uris.Devices.Temperature.all().toASCIIString() }
        links.any { it.rel.contains(Rels.GOOGLE_AUTH.value) && it.href == Uris.GoogleAuth.googleAuth().toASCIIString() }

        // asserting actions
        val actions = result.actions
        assertEquals(4, actions.size)
        actions.any { action ->
            action.name == "create-user" && action.href == URI(Uris.Users.ALL).toASCIIString() && action.method == HttpMethod.POST.name() &&
                    action.fields.size == 3 &&
                    action.fields.any { it.name == "username" && it.type == "text" } &&
                    action.fields.any { it.name == "password" && it.type == "password" } &&
                    action.fields.any { it.name == "email" && it.type == "email" }
        }

        actions.any { action ->
            action.name == "login" && action.href == URI(Uris.Users.MY_TOKEN).toASCIIString() && action.method == HttpMethod.POST.name() &&
                    action.fields.size == 2 &&
                    action.fields.any { it.name == "username" && it.type == "text" } &&
                    action.fields.any { it.name == "password" && it.type == "password" }
        }

        actions.any { action ->
            action.name == "logout" && action.href == URI(Uris.NonSemantic.logout).toASCIIString() && action.method == HttpMethod.DELETE.name()
        }

        actions.any { action ->
            action.name == "create-device" && action.href == URI(Uris.Devices.ALL).toASCIIString() && action.method == HttpMethod.POST.name() &&
                    action.fields.size == 1 &&
                    action.fields.any { it.name == "email" && it.type == "text" }
        }
    }
}