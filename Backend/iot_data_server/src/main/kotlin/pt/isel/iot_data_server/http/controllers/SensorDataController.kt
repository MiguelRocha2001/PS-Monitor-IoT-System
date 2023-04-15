package pt.isel.iot_data_server.http.controllers

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
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

    @ApiOperation(value = "All ph records", notes = "Get all ph records associated with our system associated with a device", response = PhRecordsOutputModel::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Successfully retrieved"),
        ApiResponse(code = 400, message = "Bad request - The request was not understood by the server"),
        ApiResponse(code = 404, message = "Not found - The ph records were not found")
    ])
    @GetMapping(Uris.Devices.PH.ALL_1)
    fun getPhRecords(
        @PathVariable @ApiParam(name ="ID", value = "Device ID", required = true) device_id: String
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


    @ApiOperation(value = "All temperature records", notes = "Get all temperature records associated with our system associated with a device", response = TemperatureRecordsOutputModel::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Successfully retrieved"),
        ApiResponse(code = 400, message = "Bad request - The request was not understood by the server"),
        ApiResponse(code = 404, message = "Not found - The temperature records were not found")
    ])
    @GetMapping(Uris.Devices.Temperature.ALL_1)
    fun getTemperatureRecords(
        @PathVariable @ApiParam(name ="ID", value = "Device ID", required = true) device_id: String
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