package pt.isel.iot_data_server.repository.jdbi

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.influxdb.client.write.Point
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Repository
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.CollectedDataRepository
import java.time.Instant


@Repository
class TSDBRepository : CollectedDataRepository {
    private val token = System.getenv()["INFLUX_TOKEN"]
    private val org = "isel"
    private val bucket = "my_bucket"
    private val path = "http://localhost:8086"
    private val clientThreadLocal = ThreadLocal<InfluxDBClientKotlin>()

    private fun getClient(): InfluxDBClientKotlin {
        var client = clientThreadLocal.get()
        if (client == null) {
            client = InfluxDBClientKotlinFactory.create(path, token!!.toCharArray(), org, bucket)
            clientThreadLocal.set(client)
        }
        return client
    }

    override fun getPhRecords(deviceId: DeviceId): List<PhRecord> = runBlocking {
        val query =
            """from(bucket: "my_bucket")
        |> range(start: -7d)
        |> filter(fn: (r) => r.device == "${deviceId.id}")
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

    override fun getTemperatureRecords(deviceId: DeviceId): List<TemperatureRecord> = runBlocking {
        val query =
            """from(bucket: "my_bucket")
        |> range(start: -7d)
        |> filter(fn: (r) => r.device == "${deviceId.id}")
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

    override fun savePhRecord(deviceId: DeviceId, phRecord: PhRecord) = runBlocking {
       val point = Point
           .measurement("ph")
           .addTag("device", deviceId.id.toString())
           .addField("ph_value", phRecord.value)
           .time(phRecord.timestamp, WritePrecision.NS);
        getClient().getWriteKotlinApi().writePoint(point)
    }

    override fun saveTemperatureRecord(deviceId: DeviceId, temperatureRecord: TemperatureRecord) = runBlocking {
        val point = Point
            .measurement("temperature")
            .addTag("device", deviceId.id.toString())
            .addField("temperature_value", temperatureRecord.value)
            .time(temperatureRecord.instant, WritePrecision.NS)
        getClient().getWriteKotlinApi().writePoint(point)
    }
}
