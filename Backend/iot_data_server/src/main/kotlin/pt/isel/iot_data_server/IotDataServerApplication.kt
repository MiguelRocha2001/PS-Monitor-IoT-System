package pt.isel.iot_data_server

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import org.eclipse.paho.client.mqttv3.MqttClient
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import pt.isel.iot_data_server.hive.HiveMQManager
import pt.isel.iot_data_server.hive.MqttClient.Companion.getMqttClient
import pt.isel.iot_data_server.repository.jdbi.configure
//import springfox.documentation.swagger2.annotations.EnableSwagger2


@SpringBootApplication
@OpenAPIDefinition
class IotDataServerApplication {
	@Bean
	fun jdbi() = Jdbi.create(
		PGSimpleDataSource().apply {
			setUrl(System.getenv("DB_POSTGRES_IOT_SYSTEM"))
		}
	).configure()

	// TODO: define Bean to create TS-Database (InfluxDB)

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
