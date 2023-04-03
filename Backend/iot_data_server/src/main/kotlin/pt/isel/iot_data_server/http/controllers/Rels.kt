package pt.isel.iot_data_server.http.controllers

import pt.isel.iot_data_server.http.infra.LinkRelation

object Rels {

    val SELF = LinkRelation("self")

    // ------------------- SERVER -------------------
    val SERVER_INFO = LinkRelation("server-info")

    val BATTLESHIPS_STATISTICS = LinkRelation("battleships-statistics")

    // ------------------- USERS -------------------
    val TOKEN = LinkRelation("token")
    val USER_BY_ID = LinkRelation("user")
    val USER_HOME = LinkRelation("user-home")
    val REGISTER = LinkRelation("register")
    val USERS_STATS = LinkRelation("user-stats")
    val IS_LOGGED_IN = LinkRelation("is-logged-in")
    val ME = LinkRelation("me")

    // ------------------- Devices -------------------
    val DEVICES = LinkRelation("devices")
    val DEVICE = LinkRelation("device")

    // ------------------- PH -------------------
    val PH = LinkRelation("ph")
    val PH_DATA = LinkRelation("ph-data")
    val PH_BY_ID = LinkRelation("ph-by-id")

    // ------------------- Temperature -------------------
    val TEMPERATURE = LinkRelation("temperature")
    val TEMPERATURE_DATA = LinkRelation("temperature-data")
    val TEMPERATURE_BY_ID = LinkRelation("temperature-by-id")
}