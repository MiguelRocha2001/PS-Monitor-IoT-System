package pt.isel.iot_data_server.http.model.sensor_data

import pt.isel.iot_data_server.domain.*


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
    val value: Int,
    val timestamp: String
)

fun TemperatureRecord.toOutputModel() = TemperatureRecordOutputModel(
    value = this.value,
    timestamp = this.instant.toString()
)

data class HumidityRecordsOutputModel(val records: List<HumidityRecordOutputModel>) {
    companion object {
        fun from(records: List<HumidityRecord>) = HumidityRecordsOutputModel(records.map { it.toOutputModel() })
    }
}

data class HumidityRecordOutputModel(
    val value: Double,
    val timestamp: String
)

fun HumidityRecord.toOutputModel() = HumidityRecordOutputModel(
    value = this.value,
    timestamp = this.instant.toString()
)

data class WaterFlowRecordsOutputModel(val records: List<WaterFlowRecordOutputModel>) {
    companion object {
        fun from(records: List<WaterFlowRecord>) = WaterFlowRecordsOutputModel(records.map { it.toOutputModel() })
    }
}

data class WaterFlowRecordOutputModel(
    val value: Int,
    val timestamp: String
)

fun WaterFlowRecord.toOutputModel() = WaterFlowRecordOutputModel(
    value = this.value,
    timestamp = this.instant.toString()
)

data class WaterLevelRecordsOutputModel(val records: List<WaterLevelRecordOutputModel>) {
    companion object {
        fun from(records: List<WaterLevelRecord>) = WaterLevelRecordsOutputModel(records.map { it.toOutputModel() })
    }
}

data class WaterLevelRecordOutputModel(
    val value: Int,
    val timestamp: String
)

fun WaterLevelRecord.toOutputModel() = WaterLevelRecordOutputModel(
    value = this.value,
    timestamp = this.instant.toString()
)