package pt.isel.iot_data_server.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.repository.ServerRepository

class JdbiServerRepository(handle: Handle) : ServerRepository {
    override fun createDevice(device: Device) {
        TODO("Not yet implemented")
    }

    override fun savePhRecord(deviceId: DeviceId, phRecord: PhRecord) {
        TODO("Not yet implemented")
    }
}