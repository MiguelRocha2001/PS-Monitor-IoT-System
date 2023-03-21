package pt.isel.iot_data_server.http.hypermedia.actions_links.user

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import pt.isel.iot_data_server.http.controllers.Rels
import pt.isel.iot_data_server.http.controllers.Uris
import pt.isel.iot_data_server.http.infra.SirenBuilderScope
import java.net.URI

fun createUserSirenAction(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.action(
        name = "create-user",
        href = URI(Uris.Users.TOKEN),
        method = HttpMethod.GET,
        type = MediaType.APPLICATION_JSON
    ) {
        this.textField("username")
        this.textField("password")
    }

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

fun createLogoutSirenAction(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.action(
        name = "logout",
        href = URI(Uris.Users.TOKEN),
        method = HttpMethod.DELETE,
        type = MediaType.APPLICATION_JSON
    ) {
        this.textField("username")
        this.textField("password")
    }

fun createIsLoggedInLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = URI(Uris.Users.ME),
        rel = Rels.IS_LOGGED_IN
    )