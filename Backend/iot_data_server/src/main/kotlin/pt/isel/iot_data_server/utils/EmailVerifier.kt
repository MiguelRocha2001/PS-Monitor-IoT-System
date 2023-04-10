package pt.isel.iot_data_server.utils


fun emailVerifier(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@(.+)\$")
    return emailRegex.matches(email)
}