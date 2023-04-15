package pt.isel.iot_data_server.http.controllers

import pt.isel.iot_data_server.http.infra.LinkRelation

object Rels {

    val SELF = LinkRelation("self")

    // ------------------- Google Auth -------------------
    val GOOGLE_AUTH = LinkRelation("google-login")

    // ------------------- USERS -------------------
    val IS_LOGGED_IN = LinkRelation("is-logged-in")
    val ME = LinkRelation("users-me")

    // ------------------- Devices -------------------
    val DEVICES = LinkRelation("devices")
    val DEVICE_BY_ID = LinkRelation("device-by-id")

    // ------------------- PH -------------------
    val PH_DATA = LinkRelation("ph-data")

    // ------------------- Temperature -------------------
    val TEMPERATURE_DATA = LinkRelation("temperature-data")
}