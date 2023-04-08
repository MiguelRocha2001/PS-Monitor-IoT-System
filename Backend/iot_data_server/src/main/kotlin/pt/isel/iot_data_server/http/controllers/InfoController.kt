package pt.isel.iot_data_server.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.iot_data_server.http.SirenMediaType
import pt.isel.iot_data_server.http.hypermedia.*
import pt.isel.iot_data_server.http.infra.siren

@RestController
class InfoController(

) {
    @GetMapping("/siren-info")
    fun getSirenInfo(): ResponseEntity<*> {
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(siren(Unit) {
                clazz("siren-info")
                createUserSirenAction(this)
                createTokenSirenAction(this)
                createLogoutSirenAction(this)
                isLoggedInLink(this)
                getMeLink(this)
                getDevicesLink(this)
                getNewDeviceLink(this)
                getDeviceLink(this)
                createDeviceAction(this)
                getPhLink(this)
                getTemperatureLink(this)
            })
    }
}