package pt.isel.iot_data_server

import HiveMQManager
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
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

fun main(args: Array<String>) {
	runApplication<IotDataServerApplication>(*args)
}
