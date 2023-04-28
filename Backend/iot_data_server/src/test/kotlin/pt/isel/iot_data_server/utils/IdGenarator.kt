package pt.isel.iot_data_server.utils

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

fun generateRandomName(): String {
    val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..10)
        .map { chars.random() }
        .joinToString("")
}

fun generateRandomEmail(): String {
    return "${generateRandomName()}@${generateRandomName()}.com"
}

fun generateRandomPassword(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#\$%^&+=" // caracteres permitidos na senha
    val passwordRegexPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$" // padrão de expressão regular
    val password = StringBuilder()

    while (!password.toString().matches(passwordRegexPattern.toRegex())) {
        password.clear()
        for (i in 0 until 12) { // tamanho da senha desejada
            password.append(chars.random())
        }
    }

    return password.toString()
}

fun generateRandomPh(): Double {
    val random = Random()
    return random.nextDouble(0.1, 13.9)
}

fun generateRandomTemperature(): Double {
    val random = Random()
    return random.nextDouble(0.0, 50.0)
}

fun getRandomInstantWithinLastWeek(): Instant {
    // Get the current instant
    val now = Instant.now()

    // Calculate the timestamp exactly one week ago from now
    val oneWeekAgo = now.minus(7, ChronoUnit.DAYS).epochSecond

    // Calculate the total number of seconds in the last week
    val totalSecondsLastWeek = ChronoUnit.SECONDS.between(Instant.ofEpochSecond(oneWeekAgo), now)

    // Generate a random number of seconds within the last week
    val randomSeconds = Random().nextLong(totalSecondsLastWeek)

    // Return the randomly generated instant
    return Instant.ofEpochSecond(oneWeekAgo + randomSeconds)
}

