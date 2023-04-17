package pt.isel.iot_data_server.http.controllers


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.http.DeviceInputModel
import pt.isel.iot_data_server.http.SirenMediaType
import pt.isel.iot_data_server.http.infra.siren
import pt.isel.iot_data_server.http.model.device.CreateDeviceOutputModel
import pt.isel.iot_data_server.http.model.device.DeviceOutputModel
import pt.isel.iot_data_server.http.model.device.DevicesOutputModel
import pt.isel.iot_data_server.http.model.device.toDeviceOutputModel
import pt.isel.iot_data_server.http.model.map
import pt.isel.iot_data_server.service.device.DeviceService
import java.util.*

@Tag(name = "Devices", description = "The Devices API")
@RestController
class DeviceController(
    val service: DeviceService
) {

    @Operation(summary = "All devices", description = "Get all devices associated with our system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = [Content(mediaType = "application/vnd.siren+json", schema = Schema(implementation = DevicesOutputModel::class))])
    @ApiResponse(responseCode = "400", description = "Bad request - The devices were not found")
    @ApiResponse(responseCode = "404", description = "Not found - The devices were not found") //FIXME: o QUE RETONAR SE FOR ERRO
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

    @Operation(summary = "Device by id", description = "Get a device associated with  id")
    @ApiResponse(responseCode = "200", description = "Device found", content = [Content(mediaType = "application/vnd.siren+json", schema = Schema(implementation = DeviceOutputModel::class))])
    @ApiResponse(responseCode = "404", description = "Device not found", content = [Content(mediaType = "application/vnd.siren+json", schema = Schema(implementation = DeviceOutputModel::class))])
    @GetMapping(Uris.Devices.BY_ID1)
    fun getDeviceById(
        @PathVariable device_id: String
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

    @Operation(summary = "Add device", description = "Add a device associated with  email")
    @ApiResponse(responseCode = "201", description = "Device created", content = [Content(mediaType = "application/vnd.siren+json", schema = Schema(implementation = CreateDeviceOutputModel::class))])
    @ApiResponse(responseCode = "400", description = "Bad request - The device was not created, check if given parameters are valid")
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


    @Operation(summary = "Device by email", description = "Get a device associated with  email")
    @ApiResponse(responseCode = "200", description = "Device found", content = [Content(mediaType = "application/vnd.siren+json", schema = Schema(implementation = DeviceOutputModel::class))])
    @ApiResponse(responseCode = "400", description = "Bad request - The request was not valid, check the given email")
    @GetMapping(Uris.Devices.BY_EMAIL)
    fun getDevicesByEmail(
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
}