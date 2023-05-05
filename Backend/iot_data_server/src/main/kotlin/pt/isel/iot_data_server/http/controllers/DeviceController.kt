package pt.isel.iot_data_server.http.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.http.DeviceInputModel
import pt.isel.iot_data_server.http.SirenMediaType
import pt.isel.iot_data_server.http.infra.siren
import pt.isel.iot_data_server.http.model.Problem
import pt.isel.iot_data_server.http.model.device.*
import pt.isel.iot_data_server.http.model.map
import pt.isel.iot_data_server.service.device.DeviceService
import java.util.*

@Tag(name = "Devices", description = "The Devices API")
@RestController
class DeviceController(
    val service: DeviceService
) {
    @Operation(summary = "Add device", description = "Add a device associated with  email")
    @ApiResponse(responseCode = "201", description = "Device created", content = [Content(mediaType = "application/vnd.siren+json", schema = Schema(implementation = CreateDeviceOutputModel::class))])
    @ApiResponse(responseCode = "400", description = "Bad request - Invalid email", content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = Problem::class))])
    @PostMapping(Uris.Devices.ALL)
    fun addDevice(
        @RequestBody deviceModel: DeviceInputModel,
        user: User
    ): ResponseEntity<*> {
        val result = service.addDevice(user.id, deviceModel.email)
        return result.map { deviceId ->
            ResponseEntity.status(201)
                .contentType(SirenMediaType)
                .header("Location", Uris.Devices.byId(deviceId).toASCIIString())
                .body(siren(
                    CreateDeviceOutputModel(deviceId)
                ) {
                    clazz("device-id")
                })
        }
    }

    @Operation(summary = "All devices", description = "Get all devices associated with our system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = [Content(mediaType = "application/vnd.siren+json", schema = Schema(implementation = DevicesOutputModel::class))])
    @ApiResponse(responseCode = "401", description = "Not authorized", content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = Problem::class))])
    @GetMapping(Uris.Devices.ALL)
    fun getDevices(
        user: User,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) limit: Int?
    ): ResponseEntity<*> {
        val result = service.getUserDevices(user.id, page, limit)
        return result.map {
            ResponseEntity.status(200)
                .contentType(SirenMediaType)
                .body(siren(
                    DevicesOutputModel.from(it)
                ) {
                    clazz("devices")
                })
        }
    }

    @GetMapping(Uris.Devices.ALL)
    fun getAllDevices(
        user: User,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) limit: Int?
    ): ResponseEntity<*> {
        return checkRole(user, "admin") {
            val result = service.getAllDevices(page, limit)
            result.map {
                ResponseEntity.status(200)
                    .contentType(SirenMediaType)
                    .body(siren(
                        DevicesOutputModel.from(it)
                    ) {
                        clazz("devices")
                    })
            }
        }
    }

    @GetMapping(Uris.Devices.COUNT)
    fun getDeviceCount(
        user: User
    ): ResponseEntity<*> {
        val result = service.getDeviceCount(user.id)
        return result.map { deviceCount ->
            ResponseEntity.status(200)
                .contentType(SirenMediaType)
                .body(siren(
                    DeviceCountOutputModel(deviceCount)
                ) {
                    clazz("device-count")
                })
        }
    }

    @Operation(summary = "Device by email", description = "Get a device associated with  email")
    @ApiResponse(responseCode = "200", description = "Device found", content = [Content(mediaType = "application/vnd.siren+json", schema = Schema(implementation = DeviceOutputModel::class))])
    @ApiResponse(responseCode = "400", description = "Bad request - The request was not valid, check the given email", content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = Problem::class))])
    @GetMapping(Uris.Devices.BY_EMAIL)
    fun getDevicesByEmail(
        user: User,
        @PathVariable email: String
    ): ResponseEntity<*> {
        val devices = service.getDevicesByOwnerEmail(email)
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(siren(
                DevicesOutputModel.from(devices)
            ) {
                clazz("devices")
            })
    }

    @Operation(summary = "Device by id", description = "Get a device associated with  id")
    @ApiResponse(responseCode = "200", description = "Device found", content = [Content(mediaType = "application/vnd.siren+json", schema = Schema(implementation = DeviceOutputModel::class))])
    @ApiResponse(responseCode = "400", description = "Device not found", content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = Problem::class))])
    @GetMapping(Uris.Devices.BY_ID1)
    fun getDeviceById(
        user: User,
        @PathVariable device_id: String
    ): ResponseEntity<*> {
        val device = service.getUserDeviceById(user.id, device_id)
        return device.map {
            ResponseEntity.status(200)
                .contentType(SirenMediaType)
                .body(siren(it.toDeviceOutputModel()) {
                    clazz("device")
                })
        }
    }
}