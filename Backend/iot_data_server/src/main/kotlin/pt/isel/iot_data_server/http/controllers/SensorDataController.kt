package pt.isel.iot_data_server.http.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
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

@Tag(name = "Sensor data", description = "Sensor data API")
@RestController
class SensorDataController(
    val service: SensorDataService
) {

    @Operation(summary = "Get Ph records", description = "Get all ph records associated with a device")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    @ApiResponse(responseCode = "400", description = "Bad request - The request was not valid")
    @ApiResponse(responseCode = "404", description = "Not found - The ph records were not found")
    @GetMapping(Uris.Devices.PH.ALL_1)
    fun getPhRecords(
        @PathVariable device_id: String
    ): ResponseEntity<*> {
        val result = service.getPhRecords(DeviceId(device_id))
        return result.map {
            ResponseEntity.status(200)
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



    @Operation(summary = "Get temperature records", description = "Get all temperature records associated with a device")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    @ApiResponse(responseCode = "400", description = "Bad request - The request was not valid")
    @GetMapping(Uris.Devices.Temperature.ALL_1)
    fun getTemperatureRecords(
        @PathVariable device_id: String
    ): ResponseEntity<*> {
        val result = service.getTemperatureRecords(DeviceId(device_id))
        return result.map {
            ResponseEntity.status(200)
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