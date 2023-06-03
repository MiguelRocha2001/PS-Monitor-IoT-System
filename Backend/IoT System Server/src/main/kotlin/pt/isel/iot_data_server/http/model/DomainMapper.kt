package pt.isel.iot_data_server.http.model

import org.springframework.http.ResponseEntity
import pt.isel.iot_data_server.service.Either

val domainProblemMapper = fun(error : Error) =
        problems[error::class.simpleName] as ResponseEntity<*>

fun <L,R> Either<L, R>.map (f : (R) -> ResponseEntity<*>) : ResponseEntity<*> {
    return when(this) {
        is Either.Right -> f(this.value)
        is Either.Left -> domainProblemMapper(this.value as Error)
    }
}