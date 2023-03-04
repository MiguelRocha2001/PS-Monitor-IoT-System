package pt.isel.iot_data_server

import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.repository.local.LocalRepository
import pt.isel.iot_data_server.repository.TransactionManager

@Service
class Service(
    private val transactionManager: TransactionManager,
    val repo: LocalRepository
) {
    fun createDevice(device: Device) {
        transactionManager.run {
            repo.addDevice(device)
        }
    }
    fun savePhRecord(
        deviceId: DeviceId,
        phRecord: PhRecord
    ) {
        transactionManager.run {
            repo.addPhRecord(deviceId, phRecord)
        }
    }
}