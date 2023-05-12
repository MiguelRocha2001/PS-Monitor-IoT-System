package pt.isel.iot_data_server.http.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.http.SirenMediaType
import pt.isel.iot_data_server.http.infra.siren
import pt.isel.iot_data_server.http.model.Problem
import pt.isel.iot_data_server.http.model.map
import pt.isel.iot_data_server.http.model.sensor_data.*
import pt.isel.iot_data_server.service.sensor_data.*
import pt.isel.iot_data_server.service.user.Role
import java.util.*

@Tag(name = "Sensor data", description = "Sensor data API")
@RestController
class SensorDataController(
    val sensorDataService: SensorDataService,
    val sensorErrorService: SensorErrorService
) {
    @GetMapping(Uris.Devices.Sensor.NAMES)
    fun getSensorsAvailable(): ResponseEntity<*> {
        val result = sensorDataService.getAvailableSensors()
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .header(
                "Location",
                Uris.Devices.Sensor.all().toASCIIString()
            )
            .body(
                siren(SensorNamesOutputModel(result)) {
                    clazz("sensor-names")
                }
            )
    }

    @Operation(summary = "Get Ph records", description = "Get all ph records associated with a device")
    @ApiResponse(responseCode = "200", description = "Ph successfully retrieved", content = [Content(
        mediaType = "application/vnd.siren+json",
        schema = Schema(implementation = SensorRecordsOutputModel::class)
    )])
    @ApiResponse(responseCode = "400", description = "Device not found", content = [Content(
        mediaType = "application/problem+json",
        schema = Schema(implementation = Problem::class)
    )])
    @GetMapping(Uris.Devices.Sensor.ALL_1)
    fun getSensorRecords(
        user: User,
        @PathVariable device_id: String,
        @RequestParam("sensor-name", required = false) sensorName: String,
    ): ResponseEntity<*> {
        val result = if (user.userInfo.role === Role.ADMIN)
            sensorDataService.getSensorRecords(device_id, sensorName)
        else
            sensorDataService.getSensorRecordsIfIsOwner(device_id, user.id, sensorName)
        return result.map {
            ResponseEntity.status(200)
                .contentType(SirenMediaType)
                .header(
                    "Location",
                    Uris.Devices.Sensor.all().toASCIIString()
                )
                .body(
                    siren(SensorRecordsOutputModel.from(it)) {
                        clazz("records")
                    }
                )
        }
    }

    @GetMapping(Uris.Devices.SensorError.ALL_1)
        fun getSensorErrors(
            user: User,
            @PathVariable device_id: String
        ): ResponseEntity<*> {
            val result = if (user.userInfo.role === Role.ADMIN)
                sensorErrorService.getSensorErrorRecords(device_id)
            else {
                sensorErrorService.getSensorErrorRecordsIfIsOwner(device_id, user.id)
            }
            return result.map {
                ResponseEntity.status(200)
                    .contentType(SirenMediaType)
                    .header(
                        "Location",
                        Uris.Devices.SensorError.all().toASCIIString()
                    )
                    .body(
                        siren(SensorErrorsOutputModel.from(it)) {
                            clazz("sensor-errors")
                        }
                    )
            }
        }
}