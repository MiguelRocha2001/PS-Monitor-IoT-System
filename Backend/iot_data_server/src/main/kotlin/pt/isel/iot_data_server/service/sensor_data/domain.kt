package pt.isel.iot_data_server.service.sensor_data

import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.domain.TemperatureRecord
import pt.isel.iot_data_server.service.Either

sealed class PhDataError: Error() {
    object DeviceNotFound: PhDataError()
    data class DeviceNotBelongsToUser(val userId: String): PhDataError()
}
typealias PhDataResult = Either<PhDataError, List<PhRecord>>

sealed class TemperatureDataError: Error() {
    object DeviceNotFound: TemperatureDataError()
    data class DeviceNotBelongsToUser(val userId: String): TemperatureDataError()
}
typealias TemperatureDataResult = Either<TemperatureDataError, List<TemperatureRecord>>