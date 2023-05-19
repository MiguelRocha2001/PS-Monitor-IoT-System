package pt.isel.iot_data_server.http.model

import org.springframework.http.ResponseEntity
import java.net.URI


const val INVALID_INPUT = "Invalid Input"
const val INVALID_ARGUMENT = "Invalid argument"

/**
 * {
"type": "https://example.com/probs/out-of-credit",
"title": "You do not have enough credit.",
"detail": "Your current balance is 30, but that costs 50.",
"instance": "/account/12345/msgs/abc",
"balance": 30,
"accounts": ["/account/12345",
"/account/67890"]
}
 */

data class Problem(
        val type: URI,
        val title: String,
        val detail: String,
    ) {

    companion object {
        private const val MEDIA_TYPE = "application/problem+json"
        fun response(status: Int, problem: Problem) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body(problem)
        val DEFAULT_URI = URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/an-error-has-occurred")

        fun buildInputError(value: String) = response(
            400,
            Problem(
                DEFAULT_URI,
                INVALID_INPUT,
                "Invalid value: $value"
            )
        )

        val errorHasOccurred = Problem(
            DEFAULT_URI,
        "Error",
        "An error has occurred"
        )

        val userAlreadyExists = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-already-exists"),
            "User already exists",
            "Try another name"
        )

        val insecurePassword = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/insecure-password"),
            "Insecure Password",
            "Password needs at least 4 characters including one uppercase letter"
        )

        val invalidUsername = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/invalid-username"),
            "Invalid Username",
            "Username cannot be empty"
        )

        val userOrPasswordAreInvalid = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-or-password-are-invalid"),
            "Invalid credentials",
           "Invalid name or password"
        )

        val actionNotPermitted = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/action-not-permitted"),
            "Action not permitted",
            "Cannot perform such action"
        )

        val userNotFound = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-not-found"),
            "Invalid User",
            "User not found"
        )

        val invalidInputBody = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-not-found"),
            "Invalid input body",
            "Body is not of the expected format"
        )

        val deviceAlreadyExists = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/device-already-exists"),
            "Device already exists",
            "Device already exists"
        )
        val deviceNotFound = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/device-not-found"),
            "Device not found",
            "Device not found"
        )

        val invalidOwnerEmail = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/invalid-owner-email"),
            "Invalid owner email",
            "Invalid owner email",
        )

        val emailAlreadyExists = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/email-already-exists"),
            "Email already exists",
            "Email already exists",
        )
    }
}

val problems = mapOf(
    "UserAlreadyExists" to Problem.response(409, Problem.userAlreadyExists),

    "InsecurePassword" to Problem.response(400, Problem.insecurePassword),

    "InvalidUsername" to Problem.response(400, Problem.invalidUsername),

    "UserOrPasswordAreInvalid" to Problem.response(403, Problem.userOrPasswordAreInvalid),

    "UserNotFound" to Problem.response(404, Problem.userNotFound),

    "DeviceAlreadyExists" to Problem.response(400, Problem.deviceAlreadyExists),

    "DeviceNotFound" to Problem.response(404, Problem.deviceNotFound),

    "InvalidOwnerEmail" to Problem.response(409, Problem.invalidOwnerEmail),

    "EmailAlreadyExists" to Problem.response(409, Problem.emailAlreadyExists),

    "UserOrPasswordAreInvalid" to Problem.response(403, Problem.userOrPasswordAreInvalid),

    "DeviceNotBelongsToUser" to Problem.response(403, Problem.actionNotPermitted),
)