package pt.isel.iot_data_server.repository.tsdb

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

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
    override lateinit var token: String //is lateinit to allow for the token to be set by the environment variable, if is not defined, will be null
    override var org: String = "isel"
    override var bucket: String = "my_bucket"
    override var path: String = "http://localhost:8086"
}

@Configuration
@ConfigurationProperties(prefix = "tsdb2")
class TSDB2Config : TSDBConfigProperties {
    override lateinit var token: String
    override var org: String = "isel"
    override var bucket: String = "test_bucket"
    override var path: String = "http://localhost:8086"
}