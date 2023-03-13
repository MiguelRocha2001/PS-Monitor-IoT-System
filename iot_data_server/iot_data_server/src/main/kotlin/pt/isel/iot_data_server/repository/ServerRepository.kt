package pt.isel.iot_data_server.repository

import org.jdbi.v3.core.Handle
import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceId
import pt.isel.iot_data_server.domain.PhRecord

interface ServerRepository {
    fun createDevice(device: Device)
    fun savePhRecord(deviceId: DeviceId, phRecord: PhRecord)
}