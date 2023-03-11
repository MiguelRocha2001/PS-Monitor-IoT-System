package pt.isel.iot_data_server

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import pt.isel.iot_data_server.repository.jdbi.configure
import com.hivemq.*
import com.hivemq.configuration.ConfigurationBootstrap
import com.hivemq.configuration.info.SystemInformation
import org.apache.tomcat.util.file.ConfigFileLoader

@SpringBootApplication
class IotDataServerApplication {
	@Bean
	fun jdbi() = Jdbi.create(
		PGSimpleDataSource().apply {
			setURL(System.getenv("DB_POSTGRES_CONNECTION"))
		}
	).configure()
}

fun main(args: Array<String>) {
	runApplication<IotDataServerApplication>(*args)

	// Load the configuration
	val configFile = ConfigFileLoader().load()
	val xmlConfig = XmlConfigLoader(configFile).load()
	val systemInformation = SystemInformation()
	val bootstrap = ConfigurationBootstrap(xmlConfig, ConfigClasspathLoader(), systemInformation)
	bootstrap.start()

	// Initialize the logging service
	val loggingService = LoggingService(systemInformation)

	// Start the HiveMQ server
	val server = HiveMQServer(loggingService)
	server.start()

	// Wait for the server to finish
	server.join()
}
