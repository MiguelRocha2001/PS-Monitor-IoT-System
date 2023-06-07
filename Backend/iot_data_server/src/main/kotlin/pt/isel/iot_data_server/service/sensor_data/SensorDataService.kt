package pt.isel.iot_data_server.service.sensor_data

import org.eclipse.paho.client.mqttv3.MqttClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.isel.iot_data_server.domain.*
import pt.isel.iot_data_server.repository.tsdb.SensorDataRepo
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.service.device.DeviceService
import pt.isel.iot_data_server.service.email.EmailManager


@Service
class SensorDataService(
    private val sensorDataRepo: SensorDataRepo,
    private val deviceService: DeviceService
) {
    private val logger = LoggerFactory.getLogger(SensorDataService::class.java)

    fun getSensorRecords(deviceId: String, sensorName: String): SensorDataResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(SensorDataError.DeviceNotFound)
        else
            Either.Right(sensorDataRepo.getSensorRecords(deviceId, sensorName))
    }

    fun getSensorRecordsIfIsOwner(deviceId: String, userId: String, sensorName: String): SensorDataResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(SensorDataError.DeviceNotFound)
        else if (!deviceService.belongsToUser(deviceId, userId))
            Either.Left(SensorDataError.DeviceNotBelongsToUser(userId))
        else {
            val availableSensors = sensorDataRepo.getAvailableSensorTypes(deviceId)
            availableSensors.firstOrNull { it == sensorName }?.let {
                Either.Right(sensorDataRepo.getSensorRecords(deviceId, sensorName))
            } ?: Either.Left(SensorDataError.SensorNotFound(sensorName))
        }
    }

    fun getAvailableSensors(deviceId: String): AvailableSensorsResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(AvailableSensorsError.DeviceNotFound)
        else
            Either.Right(sensorDataRepo.getAvailableSensorTypes(deviceId))
    }

    fun getAvailableSensorsIfIsOwner(deviceId: String, userId: String): AvailableSensorsResult {
        return if (!deviceService.existsDevice(deviceId))
            Either.Left(AvailableSensorsError.DeviceNotFound)
        else if (!deviceService.belongsToUser(deviceId, userId))
            Either.Left(AvailableSensorsError.DeviceNotBelongsToUser(userId))
        else
            Either.Right(sensorDataRepo.getAvailableSensorTypes(deviceId))
    }
}