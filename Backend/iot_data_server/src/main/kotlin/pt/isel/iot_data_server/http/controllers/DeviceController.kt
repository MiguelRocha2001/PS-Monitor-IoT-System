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
import pt.isel.iot_data_server.service.device.DeviceLogService
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.user.Role
import java.util.*

@Tag(name = "Devices", description = "The Devices API")
@RestController
@RequestMapping(Uris.API)
class DeviceController(
    val service: DeviceService,
    val deviceLogService: DeviceLogService,
) {
    @Operation(summary = "Add device", description = "Add a device associated with  email")
    @ApiResponse(responseCode = "201", description = "Device created", content = [Content(mediaType = "application/vnd.siren+json", schema = Schema(implementation = CreateDeviceOutputModel::class))])
    @ApiResponse(responseCode = "400", description = "Bad request - Invalid email", content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = Problem::class))])
    @PostMapping(Uris.Users.Devices.ALL)
    @Authorization(Role.USER)
    fun createDevice(
        @RequestBody deviceModel: DeviceInputModel,
        user: User
    ): ResponseEntity<*> {
        val result = service.createDevice(user.id, deviceModel.email)
        return result.map { deviceId ->
            ResponseEntity.status(201)
                .contentType(SirenMediaType)
                .header("Location", Uris.Users.Devices.byId(deviceId).toASCIIString())
                .body(siren(
                    CreateDeviceOutputModel(deviceId)
                ) {
                    clazz("device-id")
                })
        }
    }

    @GetMapping(Uris.Users.Devices.ALL_1)
    fun getDevices(
        user: User,
        @PathVariable userId: String,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) limit: Int?,
        @RequestParam(required = false) id: String?, // id chunk
        @RequestParam(required = false) email: String?
    ): ResponseEntity<*> {
        val result = if (user.userInfo.role === Role.ADMIN) {
            service.getUserDevices(userId, page, limit, email, id)
        } else {
            service.getUserDevices(user.id, page, limit, email, id) // TODO: check if is device owner
        }
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

    @GetMapping(Uris.Users.Devices.COUNT_1)
    fun getDeviceCount(
        user: User,
        @PathVariable userId: String,
        @RequestParam(required = false) id: String?, // id chunk
        @RequestParam(required = false) email: String?
    ): ResponseEntity<*> {
        val result = if (user.userInfo.role === Role.ADMIN)
            service.getDeviceCount(userId, email, id)
        else {
            service.getDeviceCount(user.id, email, id)
        }
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

    @Operation(summary = "Device by id", description = "Get a device associated with  id")
    @ApiResponse(responseCode = "200", description = "Device found", content = [Content(mediaType = "application/vnd.siren+json", schema = Schema(implementation = DeviceOutputModel::class))])
    @ApiResponse(responseCode = "400", description = "Device not found", content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = Problem::class))])
    @GetMapping(Uris.Users.Devices.BY_ID1)
    @Authorization(Role.USER)
    fun getDeviceById(
        user: User,
        @PathVariable device_id: String
    ): ResponseEntity<*> {
        val result = if (user.userInfo.role === Role.ADMIN)
            service.getDeviceById(device_id)
        else {
            service.getDeviceByIdIfOwner(user.id, device_id)
        }
        return result.map {
            ResponseEntity.status(200)
                .contentType(SirenMediaType)
                .body(siren(it.toDeviceOutputModel()) {
                    clazz("device")
                })
        }
    }

    @GetMapping(Uris.Users.Devices.WakeUpLogs.ALL_1)
    @Authorization(Role.USER)
    fun getDeviceWakeUpLogs(
        user: User,
        @PathVariable device_id: String
    ): ResponseEntity<*> {
        val result = if (user.userInfo.role === Role.ADMIN)
            deviceLogService.getDeviceLogRecords(device_id) // TODO: check if does not belong to admin. He should have no devices
        else {
            deviceLogService.getDeviceLogRecordsIfIsOwner(device_id, user.id)
        }
        return result.map {
            ResponseEntity.status(200)
                .contentType(SirenMediaType)
                .body(
                    siren(DeviceWakeUpLogsOutputModel.from(it)) {
                        clazz("device-wake-up-logs")
                    }
                )
        }
    }
}