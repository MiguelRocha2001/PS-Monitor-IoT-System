package pt.isel.iot_data_server

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.iot_data_server.repository.jdbi.configure

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
}
