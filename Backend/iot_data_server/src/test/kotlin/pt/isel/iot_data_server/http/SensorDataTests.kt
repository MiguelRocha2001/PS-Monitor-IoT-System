package pt.isel.iot_data_server.http

import deleteAllPhMeasurements
import deleteAllTemperatureMeasurements
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.iot_data_server.http.controllers.Uris
import pt.isel.iot_data_server.repository.jdbi.configure
import pt.isel.iot_data_server.repository.tsdb.TSDBConfig


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

        //add a device  with id 12345678


        val deviceID = "12345678"
        val result = client.get().uri(Uris.Devices.PH.ALL_1,deviceID)
            .exchange()
            .expectStatus().isOk


        //CONTINUE HERE

    }

    @Test
    fun `get temperature`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        val deviceID = "12345678"

        val result = client.get().uri(Uris.Devices.Temperature.ALL_1,deviceID)
            .exchange()
            .expectStatus().isOk

        //CONTINUE HERE
    }

}
