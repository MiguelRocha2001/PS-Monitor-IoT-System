package pt.isel.iot_data_server.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pt.isel.iot_data_server.repository.tsdb.TSDBConfigProperties

@Configuration
@EnableConfigurationProperties(TSDB1Config::class, TSDB2Config::class)
class TSDBConfig {
    @Bean
    fun tsdb1Properties(): TSDBConfigProperties {
        return TSDB1Config()
    }

    @Bean
    fun tsdb2Properties(): TSDBConfigProperties {
        return TSDB2Config()
    }
}

@Primary // This is the default configuration
@Configuration
@ConfigurationProperties(prefix = "tsdb1")
class TSDB1Config : TSDBConfigProperties {
    override val token: String = System.getenv()["INFLUX_TOKEN"]?:"" //same organization,same token
    override val org: String = "isel"
    override val bucket: String = "my_bucket"
    override val path: String = "http://localhost:8086"
}

// TODO: move this to test configuration
@Configuration
@ConfigurationProperties(prefix = "tsdb2")
class TSDB2Config : TSDBConfigProperties {
    override val token: String = System.getenv()["INFLUX_TOKEN"]?:""
    override val org: String = "isel"
    override val bucket: String = "test_bucket"
    override val path: String = "http://localhost:8086"
}
