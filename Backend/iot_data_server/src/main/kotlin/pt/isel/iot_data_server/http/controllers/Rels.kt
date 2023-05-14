package pt.isel.iot_data_server.http.controllers

import pt.isel.iot_data_server.http.infra.LinkRelation

object Rels {

    val SELF = LinkRelation("self")

    // ------------------- Google Auth -------------------
    val GOOGLE_AUTH = LinkRelation("google-login")

    // ------------------- USERS -------------------
    val IS_LOGGED_IN = LinkRelation("is-logged-in")
    val ME = LinkRelation("users-me")
    val IS_EMAIL_ALREADY_REGISTERED = LinkRelation("is-email-already-registered")

    //--------------------VERIFICATION ---------------
    val VERIFY_CODE = LinkRelation("verify-code")

    // ------------------- Devices -------------------
    val DEVICES = LinkRelation("devices")
    val DEVICE_COUNT = LinkRelation("device-count")
    val DEVICE_BY_ID = LinkRelation("device-by-id")
    val FILTERED_DEVICES = LinkRelation("filtered-devices")
    val FILTERED_DEVICES_COUNT = LinkRelation("filtered-devices-count")

    // ------------------- Sensor Data -------------------
    val SENSOR_DATA = LinkRelation("sensor-data")

    // ------------------- PH -------------------
    val PH_DATA = LinkRelation("ph-data")

    // ------------------- Temperature -------------------
    val TEMPERATURE_DATA = LinkRelation("temperature-data")
}