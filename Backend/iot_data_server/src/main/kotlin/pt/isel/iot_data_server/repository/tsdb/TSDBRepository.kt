package pt.isel.iot_data_server.repository.jdbi

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.influxdb.client.write.Point
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.CollectedDataRepository
import pt.isel.iot_data_server.repository.ServerRepository
import pt.isel.iot_data_server.repository.jdbi.mappers.DeviceMapper
import pt.isel.iot_data_server.repository.jdbi.mappers.UserMapper
import pt.isel.iot_data_server.repository.jdbi.mappers.toDevice
import pt.isel.iot_data_server.repository.jdbi.mappers.toUser
import java.time.Duration
import java.time.Instant

private val token = System.getenv()["INFLUX_TOKEN"]
private val org = "isel"
private val bucket = "my_bucket"
private val path = "http://localhost:8086"
val client = InfluxDBClientKotlinFactory.create(path, token!!.toCharArray(), org, bucket)

class TSDBRepository(
) : CollectedDataRepository {
    override fun getPhRecords(deviceId: DeviceId): List<PhRecord> = runBlocking {
        client.use {
            val query =
                """from(bucket: "my_bucket")
            |> range(start: -7d)
            |> filter(fn: (r) => r.device == "${deviceId.id}")
            |> filter(fn: (r) => r._measurement == "ph")
            """
            // Result is returned as a stream
            val results = client.getQueryKotlinApi().query(query)

            results
                .consumeAsFlow()
                .map { result ->
                    val value = result.getValueByKey("value") as Double
                    val timestamp = result.time ?: Instant.MIN
                    PhRecord(value, timestamp)
                }
                .toList()
        }
    }

    override fun savePhRecord(deviceId: DeviceId, phRecord: PhRecord) = runBlocking {
        client.use {
            val writeApi = it.getWriteKotlinApi()

            val point = Point
                .measurement("ph")
                .addTag("device", deviceId.id.toString())
                .addField("ph_value", phRecord.value)
                .time(Instant.now(), WritePrecision.NS);
            writeApi.writePoint(point)
        }
    }

    override fun getTemperatureRecords(deviceId: DeviceId): List<TemperatureRecord> = runBlocking {
        client.use {
            val query =
                """from(bucket: "my_bucket")
            |> range(start: -7d)
            |> filter(fn: (r) => r.device == "${deviceId.id}")
            |> filter(fn: (r) => r._measurement == "temperature")
            """
            // Result is returned as a stream
            val results = client.getQueryKotlinApi().query(query)

            results
                .consumeAsFlow()
                .map { result ->
                    val value = result.getValueByKey("value") as Double
                    val timestamp = result.time ?: Instant.MIN
                    TemperatureRecord(value, timestamp)
                }
                .toList()
        }
    }

    override fun saveTemperatureRecord(deviceId: DeviceId, temperatureRecord: TemperatureRecord) = runBlocking{
        client.use {
            val writeApi = it.getWriteKotlinApi()

            val point = Point
                .measurement("temperature")
                .addTag("device", deviceId.id.toString())
                .addField("temperature_value", temperatureRecord.value)
                .time(Instant.now(), WritePrecision.NS);
            writeApi.writePoint(point)
        }
    }

}