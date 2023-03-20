package pt.isel.iot_data_server.utils

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.iot_data_server.repository.Transaction
import pt.isel.iot_data_server.repository.TransactionManager
import pt.isel.iot_data_server.repository.jdbi.JdbiTransaction
import pt.isel.iot_data_server.repository.jdbi.configure

// TODO -> Change this to use environment variables
private val url = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=rocha"

private val jdbi = Jdbi.create(
    PGSimpleDataSource().apply {
        setURL(url)
    }
).configure()

fun testWithHandleAndRollback(block: (Handle) -> Unit) = jdbi.useTransaction<Exception> { handle ->
    block(handle)
    handle.rollback()
}

fun testWithTransactionManagerAndRollback(block: (TransactionManager) -> Unit) = jdbi.useTransaction<Exception>
{ handle ->
    val transaction = JdbiTransaction(handle)

    // a test TransactionManager that never commits
    val transactionManager = object : TransactionManager {
        override fun <R> run(block: (Transaction) -> R): R {
            return block(transaction)
            // n.b. no commit happens
        }
    }

    try {
        block(transactionManager)
    } catch (e: Exception) {
        handle.rollback()
        throw e
    }

    handle.rollback()
}