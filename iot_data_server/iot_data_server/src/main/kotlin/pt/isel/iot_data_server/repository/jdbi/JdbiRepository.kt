package pt.isel.iot_data_server.repository.jdbi

import org.jdbi.v3.core.Handle
import org.springframework.stereotype.Repository
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.repository.ServerRepository

@Repository
class JdbiRepository(
    private val handle: Handle
) : ServerRepository {
    override fun addDevice(device: Device) {
        TODO("Not yet implemented")
    }

    override fun addPhRecord(deviceId: DeviceId, phRecord: PhRecord) {
        TODO("Not yet implemented")
    }
}