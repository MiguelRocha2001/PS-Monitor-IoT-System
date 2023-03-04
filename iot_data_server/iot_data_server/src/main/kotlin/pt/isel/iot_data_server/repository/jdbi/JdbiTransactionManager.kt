package pt.isel.iot_data_server.repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component
import pt.isel.iot_data_server.repository.Transaction
import pt.isel.iot_data_server.repository.TransactionManager

@Component
class JdbiTransactionManager(
    private val jdbi: Jdbi
) : TransactionManager {

    override fun <R> run(block: (Transaction) -> R): R =
        jdbi.inTransaction<R, Exception> { handle ->
            val transaction = JdbiTransaction(handle)
            block(transaction)
        }
}