package pt.isel.iot_data_server.http

data class CreateUserInputModel(val username: String, val password: String)

data class CreateTokenInputModel(val username: String, val password: String)

data class DeviceInputModel(val email: String)

data class InputPhRecordModel(val ph: Double, val timestamp: Long)

//fun InputPhRecordModel.toPhRecord() = PhRecord(ph, Timestamp(timestamp))

data class InputTemperatureRecordModel(val temperature: Double, val timestamp: Long)

//fun InputTemperatureRecordModel.toTemperatureRecord() = TemperatureRecord(temperature, Timestamp(timestamp))