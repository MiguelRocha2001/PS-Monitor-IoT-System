package pt.isel.iot_data_server.service.sensor_data

import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.service.Either

sealed class PhDataError: Error() {
    object DeviceNotFound: PhDataError()
}
typealias PhDataResult = Either<PhDataError, List<PhRecord>>