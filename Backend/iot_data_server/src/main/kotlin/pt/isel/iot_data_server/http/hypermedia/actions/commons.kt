package pt.isel.iot_data_server.http.hypermedia.actions

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import pt.isel.iot_data_server.http.controllers.Uris
import pt.isel.iot_data_server.http.infra.SirenBuilderScope
import java.net.URI

fun createTokenSirenAction(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.action(
        name = "create-token",
        href = URI(Uris.Users.TOKEN),
        method = HttpMethod.POST,
        type = MediaType.APPLICATION_JSON
    ) {
        this.textField("username")
        this.textField("password")
    }