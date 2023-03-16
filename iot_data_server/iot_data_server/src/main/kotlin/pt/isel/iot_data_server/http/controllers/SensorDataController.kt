package pt.isel.iot_data_server.http.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
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
    @PostMapping("/device/{device_id}/ph")
    fun addPhRecord(
        @PathVariable device_id: String,
        @RequestBody phRecordModel: InputPhRecordModel
    ) {
        val deviceId = DeviceId(UUID.fromString(device_id))
        service.savePhRecord(deviceId, phRecordModel.toPhRecord())
    }

    @GetMapping("/device/{device_id}/ph")
    fun getPhRecords(
        @PathVariable device_id: String
    ) {
        val deviceId = DeviceId(UUID.fromString(device_id))
        service.getPhRecords(deviceId)
    }

    @PostMapping("/device/{device_id}/temperature")
    fun addTemperatureRecord(
        @PathVariable device_id: String,
        @RequestBody temperatureRecordModel: InputTemperatureRecordModel
    ) {
        val deviceId = DeviceId(UUID.fromString(device_id))
        service.saveTemperatureRecord(deviceId, temperatureRecordModel.toTemperatureRecord())
    }

    @GetMapping("/device/{device_id}/temperature")
    fun getTemperatureRecords(
        @PathVariable device_id: String
    ) {
        val deviceId = DeviceId(UUID.fromString(device_id))
        service.getTemperatureRecords(deviceId)
    }
}