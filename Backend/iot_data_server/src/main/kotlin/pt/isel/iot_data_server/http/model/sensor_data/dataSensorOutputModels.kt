package pt.isel.iot_data_server.http.model.sensor_data

import pt.isel.iot_data_server.domain.*

data class SensorNamesOutputModel(val sensorNames: List<String>) {
    companion object {
        fun from(sensorNames: List<String>) = SensorNamesOutputModel(sensorNames)
    }
}

data class SensorRecordsOutputModel(val records: List<SensorRecordOutputModel>) {
    companion object {
        fun from(records: List<SensorRecord>) = SensorRecordsOutputModel(records.map { it.toSensorOutputModel() })
    }
}
data class SensorRecordOutputModel(
    val value: Double,
    val timestamp: Long,
    val sensorName: String
)

fun SensorRecord.toSensorOutputModel() = SensorRecordOutputModel(
    value = this.value,
    timestamp = this.instant.epochSecond,
    sensorName = this.type
)

data class SensorErrorsOutputModel(val errors: List<SensorErrorRecordOutputModel>) {
    companion object {
        fun from(errors: List<SensorErrorRecord>) = SensorErrorsOutputModel(errors.map { it.toSensorErrorRecordOutputModel() })
    }
}

data class SensorErrorRecordOutputModel(
    val sensorName: String,
    val timestamp: Long
)

fun SensorErrorRecord.toSensorErrorRecordOutputModel() = SensorErrorRecordOutputModel(
    sensorName = this.sensorName,
    timestamp = this.instant.epochSecond
)