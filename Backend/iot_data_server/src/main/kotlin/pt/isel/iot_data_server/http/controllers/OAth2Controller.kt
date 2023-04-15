package pt.isel.iot_data_server.http.controllers

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
class OAth2Controller {
    @GetMapping(Uris.GoogleAuth.GOOGLE_AUTH)
    fun getOidcUserPrincipal(
        @AuthenticationPrincipal principal: OidcUser?
    ): OidcUser? {
        return principal
    }
}