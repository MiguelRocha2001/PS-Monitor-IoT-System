package pt.isel.iot_data_server.utils

fun String.trimJsonString() = this
    .replace("\n", "")
    .replace("\r", "")
    .replace("{", "")
    .replace("}", "")
    .replace(" ", "")