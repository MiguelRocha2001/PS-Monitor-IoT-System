package pt.isel.iot_data_server.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.http.SirenMediaType
import pt.isel.iot_data_server.http.infra.siren
import pt.isel.iot_data_server.http.model.map
import pt.isel.iot_data_server.http.model.sensor_data.PhRecordsOutputModel
import pt.isel.iot_data_server.http.model.sensor_data.TemperatureRecordsOutputModel
import pt.isel.iot_data_server.service.sensor_data.SensorDataService
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
                    siren(PhRecordsOutputModel.from(it)) {
                        clazz("ph-records")
                    }
                )
        }
    }


    @GetMapping(Uris.Devices.Temperature.ALL_1)
    fun getTemperatureRecords(
        @PathVariable device_id: String
    ): ResponseEntity<*> {
        val result = service.getTemperatureRecords(DeviceId(device_id))
        return result.map {
            ResponseEntity.status(201)
                .contentType(SirenMediaType)
                .header(
                    "Location",
                    Uris.Devices.Temperature.all().toASCIIString()
                )
                .body(
                    siren(TemperatureRecordsOutputModel.from(it)) {
                        clazz("temperature-records")
                    }
                )
        }
    }




}