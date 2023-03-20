package pt.isel.iot_data_server.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.iot_data_server.http.SirenMediaType
import pt.isel.iot_data_server.http.hypermedia.actions.createTokenSirenAction
import pt.isel.iot_data_server.http.infra.siren
import pt.isel.iot_data_server.http.model.map
import pt.isel.iot_data_server.http.model.user.UserCreateOutputModel

@RestController
class InfoController(

) {
    @GetMapping("/siren-info")
    fun getSirenInfo(): ResponseEntity<*> {
        return ResponseEntity.status(201)
            .contentType(SirenMediaType)
            .body(siren(Unit) {
                link(Uris.Users.create(), Rels.SELF)
                createTokenSirenAction(this)
                clazz("users")
            })
    }
}