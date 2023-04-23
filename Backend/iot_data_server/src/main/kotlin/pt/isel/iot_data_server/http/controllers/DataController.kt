package pt.isel.iot_data_server.http.controllers

import org.springframework.web.bind.annotation.*
import pt.isel.iot_data_server.http.model.user.*
import pt.isel.iot_data_server.service.DataEraserService
import java.util.*

@RestController
class DataController(
    val service: DataEraserService
) {
    /**
     * Used only in integration tests
     */
    @DeleteMapping(Uris.Data.ALL)
    fun reset() {
        service.eraseAllData()
    }
}