package pt.isel.iot_data_server

import HiveMQManager
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.daw.dawbattleshipgame.http.pipeline.UserArgumentResolver
import pt.isel.iot_data_server.http.pipeline.AuthenticationInterceptor
import pt.isel.iot_data_server.repository.jdbi.configure
import java.sql.Time
import kotlin.concurrent.thread

@SpringBootApplication
class IotDataServerApplication {
	@Bean
	fun jdbi() = Jdbi.create(
		PGSimpleDataSource().apply {
			setURL("jdbc:postgresql://localhost:5432/postgres?password=rocha")
		}
	).configure()

	@Bean
	fun hiveMQManager() = HiveMQManager()
}

@Configuration
class AppConfig {

	@Autowired
	lateinit var hiveMQManager: HiveMQManager

	@PostConstruct
	fun init() {
		hiveMQManager.start()
	}

	@PreDestroy
	fun destroy() {
		hiveMQManager.stop()
	}
}

@Configuration
class PipelineConfigurer(
	val authenticationInterceptor: AuthenticationInterceptor
) : WebMvcConfigurer {

	override fun addInterceptors(registry: InterceptorRegistry) {
		registry.addInterceptor(authenticationInterceptor)
	}
}

fun main(args: Array<String>) {
	runApplication<IotDataServerApplication>(*args)
}
