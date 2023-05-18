package pt.isel.iot_data_server.http.hypermedia

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import pt.isel.iot_data_server.http.controllers.Rels
import pt.isel.iot_data_server.http.controllers.Uris
import pt.isel.iot_data_server.http.infra.SirenBuilderScope
import java.net.URI

fun createUserSirenAction(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.action(
        name = "create-user",
        href = URI(Uris.Users.ALL),
        method = HttpMethod.POST,
        type = MediaType.APPLICATION_JSON
    ) {
        this.textField("email")
        this.textField("password")
    }

fun createTokenSirenAction(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.action(
        name = "login",
        href = URI(Uris.Users.MY_TOKEN),
        method = HttpMethod.POST,
        type = MediaType.APPLICATION_JSON
    ) {
        this.textField("email")
        this.textField("password")
    }

fun createLogoutSirenAction(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.action(
        name = "logout",
        href = URI(Uris.NonSemantic.logout),
        method = HttpMethod.DELETE,
        type = MediaType.APPLICATION_JSON
    ) {}

fun createDeviceAction(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.action(
        name = "create-device",
        href = Uris.Devices.all(),
        method = HttpMethod.POST,
        type = MediaType.APPLICATION_JSON
    ) {
        this.textField("email")
    }

fun getVerificationCodeAction(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.action(
        href = URI(Uris.Verification.GENERATE),
        name = "generate-and-send-code",
        method = HttpMethod.POST,
        type = MediaType.APPLICATION_JSON
    ){
    }
