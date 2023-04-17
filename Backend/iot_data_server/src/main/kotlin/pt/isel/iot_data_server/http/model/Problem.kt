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

        val userAlreadyInQueue = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-already-in-queue"),
                "User already in a queue",
                "User is already in queue, unable to perform operation"
        )

        val userAlreadyInGame = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-already-in-game"),
             "User already in game",
             "User is already in a game, unable to perform operation"
        )

        val userInGameQueue = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-in-game-queue"),
            "User in game queue",
            "User is still in game queue"
        )

        val gameNotFound = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/game-not-found"),
            "Game not found",
            "Unable to find such game"
        )

        val actionNotPermitted = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/action-not-permitted"),
            "Action not permitted",
            "Cannot perform such action"
        )

        val invalidMove = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/action-not-permitted"),
            "Invalid move",
            "Invalid move"
        )

        val invalidShot = Problem(
                URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/action-not-permitted"),
                "Invalid Shot",
                "Invalid Shot"
        )

        val noShotWasSelected = Problem(
                URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/action-not-permitted"),
                "Empty shot",
                "No shot was selected"
        )

        val userNotFound = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-not-found"),
            "Invalid user id",
            "User not found"
        )

        val invalidInputBody = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-not-found"),
            "Invalid input body",
            "Body is not of the expected format"
        )

        val notAllShipsPlaced = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-not-found"),
            "Not all ships placed",
            "Cannot proceed with the confirmation while all of the ships are not placed"
        )
        val boardIsConfirmed = Problem(
                URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/confirmed-board"),
                "Board is already confirmed",
                "Unable to perform operation, board is already confirmed"
        )
        val notInGameQueue = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/not-in-game-queue"),
            "User not in game queue",
            "User is not in game queue"
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
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/device-not-found"),
            "Invalid owner email",
            "Invalid owner email",
        )
    }
}

val problems = mapOf(
    "UserAlreadyExists" to Problem.response(400, Problem.userAlreadyExists),

    "BoardIsConfirmed" to Problem.response(405, Problem.boardIsConfirmed),

    "InsecurePassword" to Problem.response(400, Problem.insecurePassword),

    "InvalidUsername" to Problem.response(400, Problem.invalidUsername),

    "UserOrPasswordAreInvalid" to Problem.response(403, Problem.userOrPasswordAreInvalid),

    "UserAlreadyInQueue" to Problem.response(405, Problem.userAlreadyInQueue),

    "UserAlreadyInGame" to Problem.response(405, Problem.userAlreadyInGame),

    "UserInGameQueue" to Problem.response(404, Problem.userInGameQueue),

    "GameNotFound" to Problem.response(404, Problem.gameNotFound),

    "ActionNotPermitted" to Problem.response(405, Problem.actionNotPermitted),

    "EmptyShotsList" to Problem.response(400, Problem.noShotWasSelected),

    "InvalidMove" to Problem.response(405, Problem.invalidMove),

    "InvalidShot" to Problem.response(405, Problem.invalidShot),

    "UserNotFound" to Problem.response(404, Problem.userNotFound),

    "InvalidInputBody" to Problem.response(405, Problem.invalidInputBody),

    "NotAllShipsPlaced" to Problem.response(405, Problem.notAllShipsPlaced),

    "NotInGameQueue" to Problem.response(404, Problem.notInGameQueue),

    "DeviceAlreadyExists" to Problem.response(400, Problem.deviceAlreadyExists),

    "DeviceNotFound" to Problem.response(400, Problem.deviceNotFound),

    "InvalidOwnerEmail" to Problem.response(400, Problem.invalidOwnerEmail),

)