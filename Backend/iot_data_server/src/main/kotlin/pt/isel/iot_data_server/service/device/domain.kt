package pt.isel.iot_data_server.service.device

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.service.Either

sealed class CreateDeviceError: Error() {
    object DeviceAlreadyExists: CreateDeviceError()
    object InvalidOwnerEmail: CreateDeviceError()

}
typealias CreateDeviceResult = Either<CreateDeviceError, String>

sealed class GetDeviceError: Error() {
    object DeviceNotFound: GetDeviceError()
}

typealias GetDeviceResult = Either<GetDeviceError, Device>

sealed class DeleteDeviceError: Error() {
    object DeviceNotFound: DeleteDeviceError()
}

typealias DeleteDeviceResult = Either<DeleteDeviceError, Unit>