package pt.isel.iot_data_server.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.iot_data_server.http.SirenMediaType
import pt.isel.iot_data_server.http.hypermedia.actions_links.user.createIsLoggedInLink
import pt.isel.iot_data_server.http.hypermedia.actions_links.user.createLogoutSirenAction
import pt.isel.iot_data_server.http.hypermedia.actions_links.user.createTokenSirenAction
import pt.isel.iot_data_server.http.hypermedia.actions_links.user.createUserSirenAction
import pt.isel.iot_data_server.http.infra.siren

@RestController
class InfoController(

) {
    @GetMapping("/siren-info")
    fun getSirenInfo(): ResponseEntity<*> {
        return ResponseEntity.status(201)
            .contentType(SirenMediaType)
            .body(siren(Unit) {
                clazz("users")
                createUserSirenAction(this)
                createTokenSirenAction(this)
                createLogoutSirenAction(this)
                createIsLoggedInLink(this)
            })
    }
}