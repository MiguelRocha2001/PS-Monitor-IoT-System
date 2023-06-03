package pt.isel.iot_data_server.repository

interface Transaction {
    val userRepo: UserDataRepository
    val deviceRepo: DeviceDataRepository

    fun rollback()
    fun commit()
}