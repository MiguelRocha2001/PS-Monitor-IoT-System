package pt.isel.iot_data_server.service.device

import pt.isel.iot_data_server.service.Either

sealed class CreateDeviceError: Error() {
    object DeviceAlreadyExists: CreateDeviceError()
}
typealias CreateDeviceResult = Either<CreateDeviceError, Unit>