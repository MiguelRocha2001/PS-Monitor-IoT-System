package pt.isel.iot_data_server.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.iot_data_server.repository.StaticDataRepository
import pt.isel.iot_data_server.repository.Transaction


class JdbiTransaction(
    private val handle: Handle
) : Transaction {

    override val repository: StaticDataRepository by lazy { JdbiServerRepository(handle) }

    override fun rollback() {
        handle.rollback()
    }

    override fun commit() {
        handle.commit()
    }
}