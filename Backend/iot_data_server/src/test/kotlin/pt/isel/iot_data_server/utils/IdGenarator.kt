package pt.isel.iot_data_server.utils

fun generateRandomName(): String {
    val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..10)
        .map { chars.random() }
        .joinToString("")
}

fun generateRandomEmail(): String {
    return "${generateRandomName()}@${generateRandomName()}.com"
}

fun generateRandomMobileNumber(): Long {
    return (100000000..999999999).random().toLong()
}
