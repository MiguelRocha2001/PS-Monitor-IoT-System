package pt.isel.iot_data_server.http.hypermedia

import pt.isel.iot_data_server.http.controllers.Rels
import pt.isel.iot_data_server.http.controllers.Uris
import pt.isel.iot_data_server.http.infra.SirenBuilderScope
import java.net.URI

fun createGoogleAuthLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = Uris.GoogleAuth.googleAuth(),
        rel = Rels.GOOGLE_AUTH
    )

fun isLoggedInLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = Uris.NonSemantic.loggedIn(),
        rel = Rels.IS_LOGGED_IN
    )

fun getMeLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = URI(Uris.Users.ME),
        rel = Rels.ME
    )

fun getDevicesLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = Uris.Devices.all(),
        rel = Rels.DEVICES
    )

fun getDeviceCountLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = URI(Uris.Devices.COUNT),
        rel = Rels.DEVICE_COUNT
    )

fun getDeviceLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = Uris.Devices.byId(),
        rel = Rels.DEVICE_BY_ID
    )

@Deprecated("Deprecated in favor of getSensorDataLink")
fun getPhLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = Uris.Devices.PH.all(),
        rel = Rels.PH_DATA
    )

@Deprecated("Deprecated in favor of getSensorDataLink")
fun getTemperatureLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = Uris.Devices.Temperature.all(),
        rel = Rels.TEMPERATURE_DATA
    )

fun getSensorDataLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = URI(Uris.Devices.Sensor.ALL_1),
        rel = Rels.SENSOR_DATA
    )