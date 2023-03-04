package pt.isel.iot_data_server.http

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.Service
import java.util.*

@RestController
class Controller(
    val service: Service
) {
    @PostMapping("/device")
    fun addDevice(
        @RequestBody deviceModel: InputDeviceModel
    ) {
        service.createDevice(deviceModel.toDevice())
    }
    @PostMapping("/device/{device_id}/ph")
    fun addPhRecord(
        @PathVariable device_id: String,
        @RequestBody phRecordModel: InputPhRecordModel
    ) {
        val deviceId = DeviceId(UUID.fromString(device_id))
        service.savePhRecord(deviceId, phRecordModel.toPhRecord())
    }
}