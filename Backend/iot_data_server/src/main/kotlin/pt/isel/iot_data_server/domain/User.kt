package pt.isel.iot_data_server.domain

import java.util.regex.Pattern

data class UserInfo(val username: String, val password: String, val email: String, val mobile: String) {
    init {
        val emailRegexPattern = "^(.+)@(\\S+)$"
        require(patternMatches(email, emailRegexPattern)) { "Invalid email address" }

        val usernameMinLength = 5
        require(username.length >= usernameMinLength) { "Username must be at least $usernameMinLength characters long" }

        val passwordRegexPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$"
        require(patternMatches(password, passwordRegexPattern)) { "Password must contain at least 8 characters, including at least one uppercase letter, one lowercase letter, one number, and one special character" }

        val mobileRegexPattern = "^[+]?[0-9]{8,}$"
        require(patternMatches(mobile, mobileRegexPattern)) { "Invalid mobile number" }
    }

    private fun patternMatches(emailAddress: String, regexPattern: String): Boolean {
        return Pattern.compile(regexPattern)
            .matcher(emailAddress)
            .matches()
    }
}

data class User(val id: Int, val userInfo: UserInfo)