package pt.isel.iot_data_server.http

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId

data class CreateUserInputModel(val username: String, val password: String)

data class CreateTokenInputModel(val username: String, val password: String)

data class DeviceInputModel(val id: String, val email: String, val mobile: Long)

fun DeviceInputModel.toDevice(): Device {
    val deviceId = DeviceId(id)
    return Device(deviceId, email, mobile)
}

data class InputPhRecordModel(val ph: Double, val timestamp: Long)

//fun InputPhRecordModel.toPhRecord() = PhRecord(ph, Timestamp(timestamp))

data class InputTemperatureRecordModel(val temperature: Double, val timestamp: Long)

//fun InputTemperatureRecordModel.toTemperatureRecord() = TemperatureRecord(temperature, Timestamp(timestamp))