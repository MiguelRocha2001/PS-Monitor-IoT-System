package pt.isel.iot_data_server.repo.time_series_repo

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.write.Point
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pt.isel.iot_data_server.configuration.TSDBBuilder
import pt.isel.iot_data_server.repository.tsdb.MEASUREMENT_PREFIX
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.email.EmailManager
import pt.isel.iot_data_server.service.user.SaltPasswordOperations
import pt.isel.iot_data_server.service.user.UserService
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndDontRollback
import java.time.Instant

fun deleteAllPhMeasurements(tsdbBuilder: TSDBBuilder) {
    val client = OkHttpClient()

    // Define the JSON request body
    val json = """
        {
            "start": "2020-03-01T00:00:00Z",
            "stop": "2025-11-14T00:00:00Z",
            "predicate": "_measurement=\"ph initial\""
        }
    """.trimIndent()

    // Build the request
    val request = Request.Builder()
        .url("http://localhost:8086/api/v2/delete?org=${tsdbBuilder.org}&bucket=${tsdbBuilder.bucketName}")
        .addHeader("Authorization", "Token ${tsdbBuilder.token}")
        .addHeader("Content-Type", "application/json")
        .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
        .build()

    // Execute the request
    val response = client.newCall(request).execute()

    // Check the response
    if (response.isSuccessful) {
        println("Data deleted successfully.")
    } else {
        println("Failed to delete data. Response code: ${response.code}")
    }
    // Close the response
    response.close()
}

fun deleteAllSensorMeasurements(tsdbBuilder: TSDBBuilder, sensorName: String) {
    val client = OkHttpClient()

    // Define the JSON request body
    val json = """
        {
            "start": "2020-03-01T00:00:00Z",
            "stop": "2025-11-14T00:00:00Z",
            "predicate": "_measurement=\"${MEASUREMENT_PREFIX}$sensorName\""
        }
    """.trimIndent()

    // Build the request
    val request = Request.Builder()
        .url("http://localhost:8086/api/v2/delete?org=${tsdbBuilder.org}&bucket=${tsdbBuilder.bucketName}")
        .addHeader("Authorization", "Token ${tsdbBuilder.token}")
        .addHeader("Content-Type", "application/json")
        .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
        .build()

    // Execute the request
    val response = client.newCall(request).execute()

    // Check the response
    if (response.isSuccessful) {
        println("Data deleted successfully.")
    } else {
        println("Failed to delete data. Response code: ${response.code}")
    }
    // Close the response
    response.close()
}

fun deleteAllTemperatureMeasurements(tsdbBuilder: TSDBBuilder) {
    val client = OkHttpClient()

    // Define the JSON request body
    val json = """
        {
            "start": "2020-03-01T00:00:00Z",
            "stop": "2025-11-14T00:00:00Z",
            "predicate": "_measurement=\"temperature\""
        }
    """.trimIndent()

    // Build the request
    val request = Request.Builder()
        .url("http://localhost:8086/api/v2/delete?org=${tsdbBuilder.org}&bucket=${tsdbBuilder.bucketName}") //Substitute the URL
        .addHeader("Authorization", "Token ${tsdbBuilder.token}")
        .addHeader("Content-Type", "application/json")
        .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
        .build()

    // Execute the request
    val response = client.newCall(request).execute()

    // Check the response
    if (response.isSuccessful) {
        println("Data deleted successfully.")
    } else {
        println("Failed to delete data. Response code: ${response.code}")
    }

    // Close the response
    response.close()
}

fun insertDataInInfluxDB(client: InfluxDBClientKotlin, deviceId: String, sensorName: String, value: Double, instant: Instant) {
    val mutex = Mutex() // Use Mutex for synchronization
    runBlocking {
        mutex.withLock {
            val measurement = MEASUREMENT_PREFIX + sensorName  //TODO: Modify this line

            val point = Point(measurement)
                .addTag("device", deviceId)
                .addField("value", value)
                .time(instant, WritePrecision.NS)

            client.getWriteKotlinApi().writePoint(point)
        }
    }
}

fun deleteAllDeviceRecords() {
    testWithTransactionManagerAndDontRollback {
        val userService = UserService(it, SaltPasswordOperations(it), EmailManager())
        val service = DeviceService(it, userService)
        service.deleteAllDevices()
    }
}



