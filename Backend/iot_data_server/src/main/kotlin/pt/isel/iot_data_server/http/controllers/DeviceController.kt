package pt.isel.iot_data_server.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.http.DeviceInputModel
import pt.isel.iot_data_server.http.SirenMediaType
import pt.isel.iot_data_server.http.infra.siren
import pt.isel.iot_data_server.http.model.device.DevicesOutputModel
import pt.isel.iot_data_server.http.model.device.toOutputModel
import pt.isel.iot_data_server.http.model.map
import pt.isel.iot_data_server.http.toDevice
import pt.isel.iot_data_server.service.device.DeviceService
import java.util.*

@RestController
class DeviceController(
    val service: DeviceService
) {
    @GetMapping(Uris.Devices.ALL)
    fun getDevices(): ResponseEntity<*> {
        val devices = service.getAllDevices()
        return ResponseEntity.status(201)
            .contentType(SirenMediaType)
            .body(siren(
                DevicesOutputModel.from(devices)
            ) {
                clazz("devices")
            })
    }

    @GetMapping(Uris.Devices.BY_ID1)
    fun getDeviceById(
        @PathVariable device_id: String
    ): ResponseEntity<*> {
        val device = service.getDeviceById(DeviceId(device_id))
        return device.map {
            ResponseEntity.status(201)
                .contentType(SirenMediaType)
                .body(siren(it.toOutputModel()) {})
        }
    }

    @PostMapping(Uris.Devices.ALL)
    fun addDevice(
        @RequestBody deviceModel: DeviceInputModel
    ): ResponseEntity<*> {
        val result = service.addDevice(deviceModel.toDevice())
        return result.map {
            ResponseEntity.status(201)
                .contentType(SirenMediaType)
                .header(
                    "Location",
                    Uris.Devices.byId(deviceModel.id).toASCIIString()
                )
                .body(siren(Unit) {})
        }
    }
}