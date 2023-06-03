package pt.isel.iot_data_server.utils


fun String.removeJsonBrackets() = this
    .trim('{')
    .trim('}')

fun String.trimJsonString() = this
    .replace("\n", "")
    .replace("\r", "")
    .replace("{", "")
    .replace("}", "")

