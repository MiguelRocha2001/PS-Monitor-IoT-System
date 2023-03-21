package pt.isel.iot_data_server

import HiveMQManager
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.iot_data_server.http.pipeline.AuthenticationInterceptor
import pt.isel.iot_data_server.http.pipeline.LoggerInterceptor
import pt.isel.iot_data_server.repository.jdbi.configure


@SpringBootApplication
class IotDataServerApplication {
	@Bean
	fun jdbi() = Jdbi.create(
		PGSimpleDataSource().apply {
			// TODO -> Change this to use environment variables
			setURL("jdbc:postgresql://localhost:5432/postgres?user=postgres&password=rocha")
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
	val authenticationInterceptor: AuthenticationInterceptor,
	val loggerInterceptor: LoggerInterceptor
) : WebMvcConfigurer {

	override fun addInterceptors(registry: InterceptorRegistry) {
		registry.addInterceptor(authenticationInterceptor)
		registry.addInterceptor(loggerInterceptor)
	}
}

fun main(args: Array<String>) {
	runApplication<IotDataServerApplication>(*args)
}
