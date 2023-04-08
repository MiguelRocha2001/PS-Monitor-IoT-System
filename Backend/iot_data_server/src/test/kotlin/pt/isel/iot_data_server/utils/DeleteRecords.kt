import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pt.isel.iot_data_server.repository.tsdb.TSDBConfigProperties

fun deleteAllPhMeasurements(config: TSDBConfigProperties) {
    val client = OkHttpClient()

    // Define the JSON request body
    val json = """
        {
            "start": "2020-03-01T00:00:00Z",
            "stop": "2025-11-14T00:00:00Z",
            "predicate": "_measurement=\"ph\""
        }
    """.trimIndent()

    // Build the request
    val request = Request.Builder()
        .url("http://localhost:8086/api/v2/delete?org=${config.org}&bucket=${config.bucket}")
        .addHeader("Authorization", "Token ${config.token}")
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


fun deleteAllTemperatureMeasurements(config: TSDBConfigProperties) {
    val client = OkHttpClient()

    // Define the JSON request body
    val json = """
        {
            "start": "2020-03-01T00:00:00Z",
            "stop": "2025-11-14T00:00:00Z",
            "predicate": "_measurement=\temperature\""
        }
    """.trimIndent()

    // Build the request
    val request = Request.Builder()
        .url("http://localhost:8086/api/v2/delete?org=${config.org}&bucket=${config.bucket}")
        .addHeader("Authorization", "Token ${config.token}")
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