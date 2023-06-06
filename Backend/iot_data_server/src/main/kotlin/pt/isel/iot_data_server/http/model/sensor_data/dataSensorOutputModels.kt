package pt.isel.iot_data_server.http.model.sensor_data

import pt.isel.iot_data_server.domain.SensorErrorRecord
import pt.isel.iot_data_server.domain.SensorRecord

data class SensorNamesOutputModel(val types: List<String>) {
    companion object {
        fun from(sensorNames: List<String>) = SensorNamesOutputModel(sensorNames)
    }
}

data class SensorRecordsOutputModel(
    val type: String,
    val records: List<SensorRecordOutputModel>
) {
    companion object {
        fun from(records: List<SensorRecord>) =
            SensorRecordsOutputModel(
                type = records.first().type,
                records = records.map { it.toSensorOutputModel() }
            )
    }
}
data class SensorRecordOutputModel(
    val value: Double,
    val timestamp: Long
)

fun SensorRecord.toSensorOutputModel() = SensorRecordOutputModel(
    value = this.value,
    timestamp = this.instant.epochSecond
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
    sensorName = this.sensorType,
    timestamp = this.instant.epochSecond
)