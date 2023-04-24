package pt.isel.iot_data_server.repository.tsdb

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.influxdb.client.write.Point
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Repository
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.domain.TemperatureRecord
import pt.isel.iot_data_server.repository.CollectedDataRepository
import java.time.Instant

interface TSDBConfigProperties {
    val token: String
    val org: String
    val bucket: String
    val path: String
}

//TODO isto retorna os dados referentes a um nos ultimos 7 dias,se calhar devia ser possivel escolher o intervalo de tempo
@Repository
class TSDBRepository(
    tsdbConfig: TSDBConfigProperties
) : CollectedDataRepository {
    private val token = tsdbConfig.token
    private val org = tsdbConfig.org
    private val bucket = tsdbConfig.bucket
    private val path = tsdbConfig.path
    private val clientThreadLocal = ThreadLocal<InfluxDBClientKotlin>()
    val mutex = Mutex()
  //  val lock = ReentrantLock()

    init {
        if (token.isBlank()) {
            throw RuntimeException("INFLUX_TOKEN environment variable not set")
        }
    }

    private fun getClient(): InfluxDBClientKotlin {
        var client = clientThreadLocal.get()
        if (client == null) {
            client = InfluxDBClientKotlinFactory.create(path, token.toCharArray(), org, bucket)
            clientThreadLocal.set(client)
        }
        return client
    }

    override fun getPhRecords(deviceId: String): List<PhRecord> = runBlocking {
        mutex.withLock { // Use Mutex for synchronization
            val query =
                """from(bucket: "$bucket")
                |> range(start: -7d)
                |> filter(fn: (r) => r.device == "$deviceId")
                |> filter(fn: (r) => r._measurement == "ph")
                 """
            // Result is returned as a stream
            val results = getClient().getQueryKotlinApi().query(query)

            results.consumeAsFlow().map { result ->
                val value = result.value as Double
                val timestamp = result.time ?: Instant.MIN
                PhRecord(value, timestamp)
            }.toList()
        }
    }


    override fun getTemperatureRecords(deviceId: String): List<TemperatureRecord> = runBlocking {
        val query =
            """from(bucket: "$bucket")
        |> range(start: -7d)
        |> filter(fn: (r) => r.device == "${deviceId}")
        |> filter(fn: (r) => r._measurement == "temperature")
        """
        // Result is returned as a stream
        val results = getClient().getQueryKotlinApi().query(query)

        results.consumeAsFlow().map { result ->
            val value = result.value as Double
            val timestamp = result.time ?: Instant.MIN
            TemperatureRecord(value, timestamp)
        }.toList()
    }

    override fun getAllPhRecords(): List<PhRecord> = runBlocking {
        val query =
            """from(bucket: "$bucket")
            |> range(start: -7d)
            |> filter(fn: (r) => r._measurement == "ph")
            """
        // Result is returned as a stream
        val results = getClient().getQueryKotlinApi().query(query)

        results
            .consumeAsFlow()
            .map { result ->
                val value = result.value as Double
                val timestamp = result.time ?: Instant.MIN
                PhRecord(value, timestamp)
            }
            .toList()
    }



    override fun getAllTemperatureRecords(): List<TemperatureRecord> = runBlocking {
        val query =
            """from(bucket: "$bucket")
            |> range(start: -7d)
            |> filter(fn: (r) => r._measurement == "temperature")
            """
        // Result is returned as a stream
        val results = getClient().getQueryKotlinApi().query(query)

        results
            .consumeAsFlow()
            .map { result ->
                val value = result.value as Double
                val timestamp = result.time ?: Instant.MIN
                TemperatureRecord(value, timestamp)
            }
            .toList()
    }
    override fun savePhRecord(deviceId: String, phRecord: PhRecord) =
            runBlocking {
                mutex.withLock {
                    val point = Point
                        .measurement("ph")
                        .addTag("device", deviceId)
                        .addField("ph_value", phRecord.value)
                        .time(phRecord.instant, WritePrecision.NS)
                    getClient().getWriteKotlinApi().writePoint(point)
                }
        }

    override fun saveTemperatureRecord(deviceId: String, temperatureRecord: TemperatureRecord) = runBlocking {
        mutex.withLock {
            val point = Point
                .measurement("temperature")
                .addTag("device", deviceId)
                .addField("temperature_value", temperatureRecord.value)
                .time(temperatureRecord.instant, WritePrecision.NS)
            getClient().getWriteKotlinApi().writePoint(point)
        }
    }
}


