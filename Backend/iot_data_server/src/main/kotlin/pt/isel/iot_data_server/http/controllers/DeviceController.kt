package pt.isel.iot_data_server.http.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.http.DeviceInputModel
import pt.isel.iot_data_server.http.SirenMediaType
import pt.isel.iot_data_server.http.infra.siren
import pt.isel.iot_data_server.http.model.device.CreateDeviceOutputModel
import pt.isel.iot_data_server.http.model.device.DevicesOutputModel
import pt.isel.iot_data_server.http.model.device.toDeviceOutputModel
import pt.isel.iot_data_server.http.model.map
import pt.isel.iot_data_server.service.device.DeviceService
import java.util.*

@RestController
class DeviceController(
    val service: DeviceService
) {
    @ApiOperation(value = "All devices", notes = "Get all devices associated with our system", response = DevicesOutputModel::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Successfully retrieved"),
        ApiResponse(code = 400, message = "Bad request - The request was not understood by the server"),
        ApiResponse(code = 404, message = "Not found - The devices were not found")
    ])
    @GetMapping(Uris.Devices.ALL)
    fun getDevices(
        user: User
    ): ResponseEntity<*> {
        val devices = service.getAllDevices()
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(siren(
                DevicesOutputModel.from(devices)
            ) {
                clazz("devices")
            })
    }

    @GetMapping(Uris.Devices.BY_ID1)
    fun getDeviceById(
        @PathVariable @ApiParam(name ="ID", value = "Device ID", required = true) device_id: String
    ): ResponseEntity<*> {
        val device = service.getDeviceById(DeviceId(device_id))
        return device.map {
            ResponseEntity.status(200)
                .contentType(SirenMediaType)
                .body(siren(it.toDeviceOutputModel()) {
                    clazz("device")
                })
        }
    }

    @ApiOperation(value = "Add device", notes = "Add a new device to our system", response = CreateDeviceOutputModel::class)
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "Successfully created"),
        ApiResponse(code = 400, message = "Bad request - The request was not understood by the server")
    ])
    @PostMapping(Uris.Devices.ALL)
    fun addDevice(
        @RequestBody deviceModel: DeviceInputModel
    ): ResponseEntity<*> {
        val result = service.addDevice(deviceModel.email)
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

    @ApiOperation(value = "Get devices by email", notes = "Get all devices associated with a given email", response = DevicesOutputModel::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Successfully retrieved"),
        ApiResponse(code = 400, message = "Bad request - The request was not understood by the server"),
        ApiResponse(code = 404, message = "Not found - The devices were not found")
    ])
    @GetMapping(Uris.Devices.BY_EMAIL)
    fun getDevicesByEmail(
        @PathVariable @ApiParam(name = "email", value = "Owner's email", example = "exampleemail@email.com", required = true) email: String
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
}