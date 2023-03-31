package pt.isel.iot_data_server.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.iot_data_server.http.InputDeviceModel
import pt.isel.iot_data_server.http.SirenMediaType
import pt.isel.iot_data_server.http.hypermedia.createLogoutSirenAction
import pt.isel.iot_data_server.http.hypermedia.createTokenSirenAction
import pt.isel.iot_data_server.http.hypermedia.createUserSirenAction
import pt.isel.iot_data_server.http.infra.siren
import pt.isel.iot_data_server.http.model.map
import pt.isel.iot_data_server.http.model.user.UserCreateOutputModel
import pt.isel.iot_data_server.http.toDevice
import pt.isel.iot_data_server.service.device.DeviceService
import java.util.*

@RestController
class DeviceController(
    val service: DeviceService
) {
    @PostMapping(Uris.Devices.ALL)
    fun addDevice(
        @RequestBody deviceModel: InputDeviceModel
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