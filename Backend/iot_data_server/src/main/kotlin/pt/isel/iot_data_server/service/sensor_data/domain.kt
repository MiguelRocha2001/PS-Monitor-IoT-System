package pt.isel.iot_data_server.service.sensor_data

import pt.isel.iot_data_server.domain.*
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

sealed class HumidityDataError: Error() {
    object DeviceNotFound: HumidityDataError()
    data class DeviceNotBelongsToUser(val userId: String): HumidityDataError()
}
typealias HumidityDataResult = Either<HumidityDataError, List<HumidityRecord>>

sealed class WaterFlowDataError: Error() {
    object DeviceNotFound: WaterFlowDataError()
    data class DeviceNotBelongsToUser(val userId: String): WaterFlowDataError()
}
typealias WaterFlowDataResult = Either<WaterFlowDataError, List<WaterFlowRecord>>

sealed class WaterLevelDataError: Error() {
    object DeviceNotFound: WaterLevelDataError()
    data class DeviceNotBelongsToUser(val userId: String): WaterLevelDataError()
}
typealias WaterLevelDataResult = Either<WaterLevelDataError, List<WaterLevelRecord>>

sealed class SensorErrorDataError: Error() {
    object DeviceNotFound: SensorErrorDataError()
    data class DeviceNotBelongsToUser(val userId: String): SensorErrorDataError()
}
typealias SensorErrorDataResult = Either<SensorErrorDataError, List<SensorErrorRecord>>