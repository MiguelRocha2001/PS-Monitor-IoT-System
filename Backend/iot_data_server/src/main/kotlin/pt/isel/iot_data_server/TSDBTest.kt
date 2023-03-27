package pt.isel.iot_data_server

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.influxdb.client.write.Point
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.runBlocking
import java.time.Instant

fun main() = runBlocking {

    // You can generate an API token from the "API Tokens Tab" in the UI
    // val token = System.getenv()["INFLUX_TOKEN"]
    val token = "ghcTbN29lFBqzInm7uOUz7WwwEToT6q-w9Ly97o0puf_SNY064iY4BCzTNQ6zGRqFsYeE2PlrU97kO00C1LIZw=="
    val org = "isel"
    val bucket = "my_bucket"

    val client = InfluxDBClientKotlinFactory.create("http://localhost:8086", token!!.toCharArray(), org, bucket)
    client.use {
        val writeApi = it.getWriteKotlinApi()

        // val record = "mem,host=host1 used_percent=23.43234543"
        // writeApi.writeRecord(record, WritePrecision.NS)

        val point = Point
            .measurement("mem")
            .addTag("host", "host1")
            .addField("used_percent", 23.43234543)
            .time(Instant.now(), WritePrecision.NS);

        writeApi.writePoint(point)

        val query =
            """from(bucket: "my_bucket")
            |> range(start: -1d)
            """

        // Result is returned as a stream
        val results = client.getQueryKotlinApi().query(query)

        results
            .consumeAsFlow()
            .collect { println("$it") }
    }
}

