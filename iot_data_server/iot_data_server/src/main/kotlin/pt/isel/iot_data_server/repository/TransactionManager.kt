package pt.isel.iot_data_server.repository

import org.springframework.stereotype.Component

@Component
interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}