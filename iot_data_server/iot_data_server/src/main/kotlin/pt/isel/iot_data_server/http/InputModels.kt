package pt.isel.iot_data_server.http

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord
import java.sql.Timestamp
import java.util.UUID

data class CreateUserInputModel(val username: String, val password: String)

data class CreateTokenInputModel(val username: String, val password: String)

data class InputDeviceModel(val id: String)

fun InputDeviceModel.toDevice(): Device {
    val deviceId = DeviceId(UUID.fromString(id))
    return Device(deviceId)
}

data class InputPhRecordModel(val ph: Double, val timestamp: Long)

fun InputPhRecordModel.toPhRecord() = PhRecord(ph, Timestamp(timestamp))

data class InputTemperatureRecordModel(val temperature: Double, val timestamp: Long)

fun InputTemperatureRecordModel.toTemperatureRecord() = PhRecord(temperature, Timestamp(timestamp))