package pt.isel.iot_data_server.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.http.InputPhRecordModel
import pt.isel.iot_data_server.http.InputTemperatureRecordModel
import pt.isel.iot_data_server.http.SirenMediaType
import pt.isel.iot_data_server.http.infra.siren
import pt.isel.iot_data_server.http.model.map
import pt.isel.iot_data_server.http.model.sensor_data.PhRecordOutputModel
import pt.isel.iot_data_server.http.model.sensor_data.PhRecordsOutputModel
import pt.isel.iot_data_server.http.toDevice
import pt.isel.iot_data_server.service.SensorDataService
import java.util.*

@RestController
class SensorDataController(
    val service: SensorDataService
) {

    @GetMapping(Uris.Devices.PH.ALL_1)
    fun getPhRecords(
        @PathVariable device_id: String
    ): ResponseEntity<*> {
        val result = service.getPhRecords(DeviceId(device_id))
        return result.map {
            ResponseEntity.status(201)
                .contentType(SirenMediaType)
                .header(
                    "Location",
                    Uris.Devices.PH.all().toASCIIString()
                )
                .body(
                    siren(PhRecordsOutputModel.from(it)) {}
                )
        }
    }


    @GetMapping(Uris.Devices.Temperature.ALL_1)
    fun getTemperatureRecords(
        @PathVariable device_id: String
    ) {
        val deviceId = DeviceId(device_id)
        service.getTemperatureRecords(deviceId)
    }
}