package pt.isel.iot_data_server.configuration

import com.influxdb.client.domain.Bucket
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TSDBConfig {
    val token: String = System.getenv()["INFLUX_TOKEN"]?:"" // same organization, same token
    val org: String = "isel"
    val bucket: String = "my_bucket"
    val path: String = "http://localhost:8086"

    private val clientThreadLocal = ThreadLocal<InfluxDBClientKotlin>()

    @Bean
    fun getClient(): InfluxDBClientKotlin {
        var client = clientThreadLocal.get()
        if (client == null) {
            client = InfluxDBClientKotlinFactory.create(path, token.toCharArray(), org, bucket)
            clientThreadLocal.set(client)
        }
        return client
    }


    @Bean
    fun getBucketName(): Bucket = Bucket().name(bucket)
}