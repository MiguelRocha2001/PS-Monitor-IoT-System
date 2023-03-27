package pt.isel.iot_data_server.http.controllers

import org.springframework.web.bind.annotation.*
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.http.InputPhRecordModel
import pt.isel.iot_data_server.http.InputTemperatureRecordModel
import pt.isel.iot_data_server.http.toPhRecord
import pt.isel.iot_data_server.http.toTemperatureRecord
import pt.isel.iot_data_server.service.SensorDataService
import java.util.*

@RestController
class SensorDataController(
    val service: SensorDataService
) {
    @GetMapping(Uris.Devices.PH.ALL)
    fun getPhRecords(
        @PathVariable device_id: String
    ) {
        val deviceId = DeviceId(UUID.fromString(device_id))
        service.getPhRecords(deviceId)
    }

    @GetMapping(Uris.Devices.Temperature.ALL)
    fun getTemperatureRecords(
        @PathVariable device_id: String
    ) {
        val deviceId = DeviceId(UUID.fromString(device_id))
        service.getTemperatureRecords(deviceId)
    }
}