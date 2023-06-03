package pt.isel.iot_data_server.service.device

import pt.isel.iot_data_server.domain.Device
import pt.isel.iot_data_server.domain.DeviceWakeUpLog
import pt.isel.iot_data_server.service.Either

sealed class CreateDeviceError: Error() {
    object DeviceAlreadyExists: CreateDeviceError()
    object InvalidOwnerEmail: CreateDeviceError()

}
typealias CreateDeviceResult = Either<CreateDeviceError, String>

sealed class DeviceCountError: Error() {
    object UserNotFound: DeviceCountError()
}
typealias DeviceCountResult = Either<DeviceCountError, Int>

sealed class GetAllDevicesError: Error() {
    object UserNotFound: GetAllDevicesError()
}
typealias GetAllDevicesResult = Either<GetAllDevicesError, List<Device>>

sealed class GetDeviceError: Error() {
    object DeviceNotFound: GetDeviceError()
}
typealias GetDeviceResult = Either<GetDeviceError, Device>

sealed class DeleteDeviceError: Error() {
    object DeviceNotFound: DeleteDeviceError()
}
typealias DeleteDeviceResult = Either<DeleteDeviceError, Unit>

sealed class DeviceErrorRecordsError: Error() {
    object DeviceNotFound: DeviceErrorRecordsError()
    data class DeviceNotBelongsToUser(val userId: String): DeviceErrorRecordsError()
}
typealias DeviceErrorRecordsResult = Either<DeviceErrorRecordsError, List<DeviceWakeUpLog>>