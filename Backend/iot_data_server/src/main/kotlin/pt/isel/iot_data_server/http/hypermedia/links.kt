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

fun getDevicesByUserLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = Uris.Users.Devices.allByUser(),
        rel = Rels.DEVICES
    )

fun getUserDeviceCountLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = URI(Uris.Users.Devices.COUNT_2),
        rel = Rels.DEVICE_COUNT
    )

fun getDeviceLinkById(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = Uris.Users.Devices.byId(),
        rel = Rels.DEVICE_BY_ID
    )

fun getDeviceWakeUpLogsLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = Uris.Users.Devices.WakeUpLogs.all(),
        rel = Rels.DEVICE_WAKE_UP_LOGS
    )

fun getSensorDataLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = URI(Uris.Users.Devices.Sensor.ALL_2),
        rel = Rels.SENSOR_DATA
    )

fun getIsEmailAlreadyRegisteredLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = URI(Uris.Users.exists.BY_EMAIL_2),
        rel = Rels.IS_EMAIL_ALREADY_REGISTERED
    )

fun getVerifyCodeLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = URI(Uris.Verification.CODE),
        rel = Rels.VERIFY_CODE
    )

fun getMyAvailableDeviceSensorsLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = URI(Uris.Users.Devices.Sensor.TYPES_2),
        rel = Rels.AVAILABLE_DEVICE_SENSORS
    )

fun getUserCountLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = URI(Uris.Users.COUNT),
        rel = Rels.USER_COUNT
    )

fun getUsersLink(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.link(
        href = URI(Uris.Users.ALL),
        rel = Rels.USERS
    )