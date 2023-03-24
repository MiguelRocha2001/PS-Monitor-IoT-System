package pt.isel.iot_data_server.repository

interface Transaction {
    val repository: ServerRepository

    fun rollback()
    fun commit()
}