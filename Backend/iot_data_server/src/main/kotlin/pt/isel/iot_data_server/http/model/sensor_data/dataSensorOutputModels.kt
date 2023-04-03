package pt.isel.iot_data_server.http.model.sensor_data

import pt.isel.iot_data_server.domain.PhRecord


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
    timestamp = this.timestamp.epochSecond
)

data class TemperatureRecordOutputModel(
    val id: String,
    val value: Double,
    val timestamp: String
)