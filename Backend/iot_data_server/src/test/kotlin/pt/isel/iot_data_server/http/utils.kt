package pt.isel.iot_data_server.http

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import pt.isel.iot_data_server.repository.jdbi.configure

fun buildJdbiTest() = Jdbi.create(
    PGSimpleDataSource().apply {
        setURL(System.getenv("DB_POSTGRES_IOT_SYSTEM_TEST"))
    }
).configure()