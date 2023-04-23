package pt.isel.iot_data_server.http

import pt.isel.iot_data_server.utils.deleteAllPhMeasurements
import pt.isel.iot_data_server.utils.deleteAllTemperatureMeasurements
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.iot_data_server.http.controllers.Uris
import pt.isel.iot_data_server.repository.tsdb.TSDBConfig
import pt.isel.iot_data_server.utils.generateRandomEmail


//FIXME VER COMO SE FAZEM ESTES TESTES
//Nao estou a conseguir fazer o setup do test porque tenho de usar o bucket 2 e nao sei como fazer isso por equanto
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SensorDataTests {
    @LocalServerPort
    var port: Int = 0

    @AfterEach
    fun delete_data(){
        val testDBConfig = TSDBConfig().tsdb2Properties()
        deleteAllPhMeasurements(testDBConfig)
        deleteAllTemperatureMeasurements(testDBConfig)
    }

    @Test
    fun `get ph`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        val userToken = createUserAndLogin(email, client)
        val deviceId = create_device(email, client, userToken)

        val result = client.get().uri(Uris.Devices.PH.ALL_1, deviceId)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isOk

        // CONTINUE HERE
    }

    @Test
    fun `get temperature`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        val userToken = createUserAndLogin(email, client)
        val deviceId = create_device(email, client, userToken)

        val result = client.get().uri(Uris.Devices.Temperature.ALL_1, deviceId)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isOk

        // CONTINUE HERE
    }

}
