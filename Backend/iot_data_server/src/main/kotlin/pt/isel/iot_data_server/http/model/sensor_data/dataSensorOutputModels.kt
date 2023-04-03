package pt.isel.iot_data_server.http.model.sensor_data

import pt.isel.iot_data_server.domain.PhRecord
import pt.isel.iot_data_server.domain.TemperatureRecord


data class PhRecordsOutputModel(val records: List<PhRecordOutputModel>) {
    companion object {
        fun from(records: List<PhRecord>) = PhRecordsOutputModel(records.map { it.toOutputModel() })
    }
}
data class PhRecordOutputModel(
    val value: Double,
    val timestamp: Long
)

fun PhRecord.toOutputModel() = PhRecordOutputModel(
    value = this.value,
    timestamp = this.instant.epochSecond
)

data class TemperatureRecordsOutputModel(val records: List<TemperatureRecordOutputModel>) {
    companion object {
        fun from(records: List<TemperatureRecord>) = TemperatureRecordsOutputModel(records.map { it.toOutputModel() })
    }
}

data class TemperatureRecordOutputModel(
    val value: Double,
    val timestamp: String
)

fun TemperatureRecord.toOutputModel() = TemperatureRecordOutputModel(
    value = this.value,
    timestamp = this.instant.toString()
)