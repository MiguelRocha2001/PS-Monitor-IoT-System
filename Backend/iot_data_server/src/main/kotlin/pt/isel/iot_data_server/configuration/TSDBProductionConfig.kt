package pt.isel.iot_data_server.configuration

import com.influxdb.client.domain.Bucket
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TSDBProductionConfig {
    private val tsdbConfig = TSDBBuilder("production")
    @Bean
    fun getInfluxDBClientKotlin(): InfluxDBClientKotlin {
        return tsdbConfig.getClient()
    }
    @Bean fun getBucket(): Bucket {
        return tsdbConfig.getBucket()
    }
}

class TSDBBuilder(val bucketName: String) {
    val token: String = System.getenv()["INFLUX_TOKEN"] ?: "" // same organization, same token
    val org: String = "isel"
    val path: String = System.getenv()["INFLUX_URL"] ?: "http://localhost:8086" // same organization, same token

    private val clientThreadLocal = ThreadLocal<InfluxDBClientKotlin>()

    fun getClient(): InfluxDBClientKotlin {
        var client = clientThreadLocal.get()
        if (client == null) {
            client = InfluxDBClientKotlinFactory.create(path, token.toCharArray(), org, bucketName)
            clientThreadLocal.set(client)
        }
        return client
    }

    fun getBucket(): Bucket {
        return Bucket().name(bucketName)
    }
}

