package pt.isel.iot_data_server.configuration

import pt.isel.iot_data_server.domain.SensorInfo
import java.io.File
import java.util.logging.Logger

class SensorInfoFromFile: SensorInfo {
    private val logger = Logger.getLogger(SensorInfoFromFile::class.java.name)
    private val file = File("sensor-thresholds.txt")
    private val sensorThresholds = mutableMapOf<String, Pair<Double?, Double?>>()
    init {
        if (!file.exists()) {
            logger.info("Sensor thresholds file not found. Creating new file.")
            file.createNewFile()
            logger.info("File created. Please fill it with the sensor thresholds.")
        } else {
            logger.info("Sensor thresholds file found. Loading thresholds.")
            file.forEachLine {
                val split1 = it.split(":").map { split -> split.trim() }
                val sensorType = split1[0]
                val split2 = split1[1].split("-").map { split -> split.trim() }
                val lowerThreshold = split2[0].toDoubleOrNull()
                val upperThreshold = if (split2.size == 2) split2[1].toDoubleOrNull() else null
                sensorThresholds[sensorType] = lowerThreshold to upperThreshold
            }
            logger.info("Sensor thresholds file loading complete")
        }
    }
    override fun getUpperSensorThreshold(sensorName: String): Double? {
        return sensorThresholds[sensorName]?.first
    }

    override fun getSensorLowerThreshold(sensorName: String): Double? {
        return sensorThresholds[sensorName]?.second
    }
}