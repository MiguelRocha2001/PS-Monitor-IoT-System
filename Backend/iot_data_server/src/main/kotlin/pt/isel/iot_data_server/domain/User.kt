package pt.isel.iot_data_server.domain

import java.util.regex.Pattern

data class UserInfo(val username: String, val password: String, val email: String, val mobile: String) {
    init {
        val regexPattern = "^(.+)@(\\S+)$"
        val valid = patternMatches(email, regexPattern)
        require(valid) { "Invalid email address" }
    }

    private fun patternMatches(emailAddress: String, regexPattern: String): Boolean {
        return Pattern.compile(regexPattern)
            .matcher(emailAddress)
            .matches()
    }
}

data class User(val id: Int, val userInfo: UserInfo)