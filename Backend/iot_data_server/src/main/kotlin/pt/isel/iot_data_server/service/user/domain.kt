package pt.isel.iot_data_server.service.user

import pt.isel.iot_data_server.service.Either

enum class Role { ADMIN, USER }

sealed class CreateUserError: Error() {
    object UserAlreadyExists: CreateUserError()
    object InsecurePassword: CreateUserError()
    object InvalidUsername: CreateUserError()
    object EmailAlreadyExists: CreateUserError()
    object InvalidEmail: CreateUserError()
}
/** returns the newly created User userId, and token for authentication */
typealias UserCreationResult = Either<CreateUserError, Pair<String, String>>

sealed class TokenCreationError: Error() {
    object UserNotFound: TokenCreationError()
    object CreatedWithGoogleAuth: TokenCreationError()
    object InvalidPassword: TokenCreationError()
}
typealias TokenCreationResult = Either<TokenCreationError, String>