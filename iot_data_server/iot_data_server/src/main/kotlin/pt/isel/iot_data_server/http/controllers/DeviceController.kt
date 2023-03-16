package pt.isel.iot_data_server.http.controllers

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.http.InputDeviceModel
import pt.isel.iot_data_server.http.toDevice
import pt.isel.iot_data_server.service.DeviceService
import java.util.*

@RestController
class DeviceController(
    val service: DeviceService
) {
    @PostMapping("/device")
    fun addDevice(
        user: User,
        @RequestBody deviceModel: InputDeviceModel
    ) {
        service.createDevice(deviceModel.toDevice())
    }
}