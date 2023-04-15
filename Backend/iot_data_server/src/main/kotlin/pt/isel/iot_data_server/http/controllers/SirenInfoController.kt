package pt.isel.iot_data_server.http.controllers

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.iot_data_server.http.SirenMediaType
import pt.isel.iot_data_server.http.hypermedia.*
import pt.isel.iot_data_server.http.infra.siren

@RestController
class SirenInfoController(

) {
    @ApiOperation(value = "Siren Info", notes = "Get all the siren info", response = Unit::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Successfully retrieved"),
        ApiResponse(code = 400, message = "Bad request - The request was not understood by the server")
    ])
    @GetMapping(Uris.SirenInfo.SIREN_INFO)
    fun getSirenInfo(): ResponseEntity<*> {
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(siren(Unit) {
                clazz("siren-info")
                createGoogleAuthLink(this)
                createUserSirenAction(this)
                createTokenSirenAction(this)
                createLogoutSirenAction(this)
                isLoggedInLink(this)
                getMeLink(this)
                getDevicesLink(this)
                getDeviceLink(this)
                createDeviceAction(this)
                getPhLink(this)
                getTemperatureLink(this)
            })
    }
}