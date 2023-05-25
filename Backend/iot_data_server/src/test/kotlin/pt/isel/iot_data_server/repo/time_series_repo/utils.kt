package pt.isel.iot_data_server.repo.time_series_repo

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pt.isel.iot_data_server.configuration.TSDBBuilder
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.email.EmailManager
import pt.isel.iot_data_server.service.user.SaltPasswordOperations
import pt.isel.iot_data_server.service.user.UserService
import pt.isel.iot_data_server.utils.testWithTransactionManagerAndDontRollback

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
    val MEASUREMENT_PREFIX = "my_sensor "

    // Define the JSON request body
    val json = """
        {
            "start": "2020-03-01T00:00:00Z",
            "stop": "2025-11-14T00:00:00Z",
            "predicate": "_measurement=\"my_sensor $sensorName\""
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

fun deleteAllDeviceRecords() {
    testWithTransactionManagerAndDontRollback {
        val userService = UserService(it, SaltPasswordOperations(it), EmailManager())
        val service = DeviceService(it, userService)
        service.removeAllDevices()
    }
}



