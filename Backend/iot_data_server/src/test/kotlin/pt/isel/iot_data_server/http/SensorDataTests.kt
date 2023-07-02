package pt.isel.iot_data_server.http

import com.influxdb.client.domain.Bucket
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.iot_data_server.configuration.TSDBBuilder
import pt.isel.iot_data_server.http.controllers.Uris
import pt.isel.iot_data_server.http.infra.SirenModel
import pt.isel.iot_data_server.repo.time_series_repo.deleteAllPhMeasurements
import pt.isel.iot_data_server.repo.time_series_repo.deleteAllTemperatureMeasurements
import pt.isel.iot_data_server.utils.generateRandomEmail


//FIXME VER COMO SE FAZEM ESTES TESTES
//Nao estou a conseguir fazer o setup do test porque tenho de usar o bucket 2 e nao sei como fazer isso por equanto
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SensorDataTests {
    private val tsdbBuilder: TSDBBuilder = TSDBBuilder("test")
    @LocalServerPort
    var port: Int = 0
    @TestConfiguration
    class SensorDataTestConfiguration {
        private val tsdbBuilder: TSDBBuilder = TSDBBuilder("test")
        @Bean
        fun getInfluxDBClientKotlin(): InfluxDBClientKotlin {
            return tsdbBuilder.getClient()
        }
        @Bean fun getBucket(): Bucket {
            return tsdbBuilder.getBucket()
        }

        @Bean
        @Primary
        fun jdbiTest() = buildJdbiTest()
    }

    @BeforeEach
    fun delete_data() {
        deleteAllPhMeasurements(tsdbBuilder)
        deleteAllTemperatureMeasurements(tsdbBuilder)

        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        client.delete().uri(Uris.Data.ALL.toApiUri())
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody
    }

    @Test
    fun `Cannot get pH sensor record`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val email = generateRandomEmail()
        val (_, userToken) = createUser(email, generatePassword(1), client)
        val deviceId = create_device(email, client, userToken)

        client.get().uri((Uris.Users.Devices.Sensor.ALL_1 + "?sensor-name=ph").toApiUri(), deviceId)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isNotFound
    }
}