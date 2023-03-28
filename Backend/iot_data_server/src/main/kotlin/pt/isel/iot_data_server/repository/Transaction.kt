package pt.isel.iot_data_server.repository

interface Transaction {
    val repository: StaticDataRepository

    fun rollback()
    fun commit()
}