package pt.isel.iot_data_server

import org.eclipse.paho.client.mqttv3.MqttClient
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import pt.isel.iot_data_server.MqttClient.Companion.getMqttClient
import pt.isel.iot_data_server.repository.jdbi.configure
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


@SpringBootApplication
class IotDataServerApplication {
	@Bean
	fun jdbi() = Jdbi.create(
		PGSimpleDataSource().apply {
			setUrl(System.getenv("DB_POSTGRES_IOT_SYSTEM"))
		}
	).configure()

	@Bean("hiveMQManager")
	fun hiveMQManager() = HiveMQManager()

	@Bean
	@DependsOn("hiveMQManager")
	fun mqttClient(): MqttClient {
		val client = getMqttClient()
		client.connect()
		return client
	}
}

fun main(args: Array<String>) {
	runApplication<IotDataServerApplication>(*args)
}
