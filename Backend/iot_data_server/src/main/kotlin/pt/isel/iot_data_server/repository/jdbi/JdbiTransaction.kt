package pt.isel.iot_data_server.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.iot_data_server.repository.DeviceDataRepository
import pt.isel.iot_data_server.repository.SensorDataRepository
import pt.isel.iot_data_server.repository.Transaction
import pt.isel.iot_data_server.repository.UserDataRepository


class JdbiTransaction(
    private val handle: Handle
) : Transaction {
    override val userRepo: UserDataRepository by lazy { JdbiUserDataRepository(handle) }
    override val deviceRepo: DeviceDataRepository by lazy { JdbiDeviceDataRepository(handle) }
    override val sensorRepo: SensorDataRepository by lazy { JdbiSensorDataRepository(handle) }

    override fun rollback() {
        handle.rollback()
    }

    override fun commit() {
        handle.commit()
    }
}