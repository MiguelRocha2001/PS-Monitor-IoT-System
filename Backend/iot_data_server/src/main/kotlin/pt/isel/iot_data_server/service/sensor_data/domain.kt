package pt.isel.iot_data_server.service.sensor_data

import pt.isel.iot_data_server.domain.SensorErrorRecord
import pt.isel.iot_data_server.domain.SensorRecord
import pt.isel.iot_data_server.service.Either

sealed class SensorDataError: Error() {
    object DeviceNotFound: SensorDataError()
    data class DeviceNotBelongsToUser(val userId: String): SensorDataError()
}
typealias SensorDataResult = Either<SensorDataError, List<SensorRecord>>

sealed class SensorErrorDataError: Error() {
    object DeviceNotFound: SensorErrorDataError()
    data class DeviceNotBelongsToUser(val userId: String): SensorErrorDataError()
}
typealias SensorErrorDataResult = Either<SensorErrorDataError, List<SensorErrorRecord>>