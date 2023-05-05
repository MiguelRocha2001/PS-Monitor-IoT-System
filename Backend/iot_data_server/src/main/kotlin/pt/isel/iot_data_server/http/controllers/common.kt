package pt.isel.iot_data_server.http.controllers

import org.springframework.http.ResponseEntity
import pt.isel.iot_data_server.domain.User

fun checkRole(user: User, role: String, onAuthorized: () -> ResponseEntity<*>): ResponseEntity<*> {
    if (user.userInfo.role != role) {
        return ResponseEntity.status(401).build<Unit>()
    }
    return onAuthorized()
}