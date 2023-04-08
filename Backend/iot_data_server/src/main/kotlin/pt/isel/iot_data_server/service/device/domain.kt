package pt.isel.iot_data_server.service.device

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.service.Either

sealed class CreateDeviceError: Error() {
    object DeviceAlreadyExists: CreateDeviceError()
}
typealias CreateDeviceResult = Either<CreateDeviceError, String>

sealed class GetDeviceError: Error() {
    object DeviceNotFound: GetDeviceError()
}
typealias GetDeviceResult = Either<GetDeviceError, Device>