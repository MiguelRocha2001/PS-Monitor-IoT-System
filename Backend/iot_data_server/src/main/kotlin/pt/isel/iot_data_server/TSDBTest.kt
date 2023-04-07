package pt.isel.iot_data_server

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.service.email.EmailSender
import java.time.Instant

/*
fun main() = runBlocking {

    // You can generate an API token from the "API Tokens Tab" in the UI
    // val token = System.getenv()["INFLUX_TOKEN"]
  //  val token = "ghcTbN29lFBqzInm7uOUz7WwwEToT6q-w9Ly97o0puf_SNY064iY4BCzTNQ6zGRqFsYeE2PlrU97kO00C1LIZw=="
    val token = System.getenv()["INFLUX_TOKEN"]
    val org = "isel"
    val bucket = "my_bucket"

    val client = InfluxDBClientKotlinFactory.create("http://localhost:8086", token!!.toCharArray(), org, bucket)
    client.use {
        val writeApi = it.getWriteKotlinApi()

        // val record = "mem,host=host1 used_percent=23.43234543"
        // writeApi.writeRecord(record, WritePrecision.NS)

        val point = Point
            .measurement("ph")
            .addTag("device", "device1")
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
*/

val MIN_PH = 6.0
val emailSenderService = EmailSender()

fun main() {

  //  val ph = PhRecord(1.0, Instant.now())
  //  val device = Device(DeviceId("device1"), "psilva20019@gmail.com",2939939)
 //   sendEmailIfPhExceedsLimit(DeviceId("device1"), ph,device)
  //  val a = EmailSender()
  //  a.sendEmail("psilva20019@gmail.com","another","another")
/*
    val repository = TSDBRepository()
    val uuid = "device1"
    val deviceId = DeviceId(uuid)

    // Save a ph record
    val phRecord = PhRecord(Random.nextDouble(0.0,10.0), Instant.now())
    repository.savePhRecord(deviceId, phRecord)

    // Save a temperature record
    val temperatureRecord = TemperatureRecord(Random.nextDouble(0.0,10.0), Instant.now())
    repository.saveTemperatureRecord(deviceId, temperatureRecord)

    // Get temperature records
    val temperatureRecords = repository.getTemperatureRecords(deviceId)
    println("Temperature records: $temperatureRecords")

    // Get ph records
    val phRecords = repository.getPhRecords(deviceId)
    println("Ph records: $phRecords")
*/
}

