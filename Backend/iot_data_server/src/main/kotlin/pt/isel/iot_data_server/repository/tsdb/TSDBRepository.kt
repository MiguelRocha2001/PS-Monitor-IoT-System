package pt.isel.iot_data_server.repository.tsdb

import com.influxdb.client.domain.Bucket
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.write.Point
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Repository
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.CollectedDataRepository
import java.time.Instant

/*
TODO: check server status
    @see: https://github.com/influxdata/influxdb-client-java/tree/master/client-kotlin#advanced-usage
 */

//TODO isto retorna os dados referentes a um nos ultimos 7 dias,se calhar devia ser possivel escolher o intervalo de tempo
@Repository
class TSDBRepository(
    private val client: InfluxDBClientKotlin,
    bucket : Bucket
) : CollectedDataRepository {
    private val bucketName = bucket.name
    val mutex = Mutex() // Use Mutex for synchronization

    override fun saveSensorRecord(deviceId: String, sensorRecord: SensorRecord) = runBlocking {
        runBlocking {
            mutex.withLock {
                val point = Point(sensorRecord.type)
                    .addTag("device", deviceId)
                    .addField("value", sensorRecord.value)
                    .time(sensorRecord.instant, WritePrecision.NS)
                client.getWriteKotlinApi().writePoint(point)
            }
        }
    }

    override fun getSensorRecords(deviceId: String, sensorName: String): List<SensorRecord> = runBlocking {
        val query =
            """from(bucket: "$bucketName")
                |> range(start: -7d)
                |> filter(fn: (r) => r.device == "$deviceId")
                |> filter(fn: (r) => r._measurement == "$sensorName")
                 """
        // Result is returned as a stream
        val results = client.getQueryKotlinApi().query(query)

        results.consumeAsFlow().map { result ->
            val value = result.value as Double
            val timestamp = result.time ?: Instant.MIN
            SensorRecord(sensorName, value, timestamp)
        }.toList()
    }

    override fun getSensorNames(): List<String> = runBlocking {
        throw NotImplementedError("Not implemented yet")
    }
}


