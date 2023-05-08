package pt.isel.iot_data_server.repository

interface Transaction {
    val userRepo: UserDataRepository
    val deviceRepo: DeviceDataRepository
    val sensorRepo: SensorDataRepository

    fun rollback()
    fun commit()
}