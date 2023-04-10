package pt.isel.iot_data_server.service.user

import pt.isel.iot_data_server.service.Either


sealed class CreateUserError: Error() {
    object UserAlreadyExists: CreateUserError()
    object InsecurePassword: CreateUserError()
    object InvalidUsername: CreateUserError()
    object EmailAlreadyExists: CreateUserError()
}
/** returns the newly created User userId, and token for authentication */
typealias UserCreationResult = Either<CreateUserError, Pair<Int, String>>

sealed class TokenCreationError: Error() {
    object UserOrPasswordAreInvalid: TokenCreationError()
}
typealias TokenCreationResult = Either<TokenCreationError, String>