package pt.isel.iot_data_server.http.controllers


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.iot_data_server.http.SirenMediaType
import pt.isel.iot_data_server.http.hypermedia.*
import pt.isel.iot_data_server.http.infra.siren

@Tag(name = "Siren Info", description = "The Siren Info API")
@RestController
class ApiInfoController {
    @Operation(summary = "Siren Info", description = "Get the siren info")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    @GetMapping(Uris.SirenInfo.SIREN_INFO)
    fun getSirenInfo(): ResponseEntity<*> {
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(siren(Unit) {
                clazz("siren-info")
                createGoogleAuthLink(this)
                createUserSirenAction(this)
                getIsEmailAlreadyRegisteredLink(this)

                getVerificationCodeAction(this)
                getVerifyCodeLink(this)
                createTokenSirenAction(this)
                createLogoutSirenAction(this)
                isLoggedInLink(this)
                getMeLink(this)
                getDevicesByUserLink(this)
                getUserDeviceCountLink(this)
                getDeviceLinkById(this)
                createDeviceAction(this)
                getSensorDataLink(this)
                getMyAvailableDeviceSensorsLink(this)
            })
    }
}