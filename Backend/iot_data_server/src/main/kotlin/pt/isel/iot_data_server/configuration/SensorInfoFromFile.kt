package pt.isel.iot_data_server.configuration

import pt.isel.iot_data_server.domain.SensorInfo
import java.io.File
import java.util.logging.Logger

class SensorInfoFromFile: SensorInfo {
    private val logger = Logger.getLogger(SensorInfoFromFile::class.java.name)
    private val file = File("sensor_thresholds.txt")
    private val sensorThresholds = mutableMapOf<String, Pair<Double?, Double?>>()
    init {
        if (!file.exists()) {
            logger.info("Sensor thresholds file not found. Creating new file.")
            file.createNewFile()
            logger.info("File created. Please fill it with the sensor thresholds.")
        } else {
            logger.info("Sensor thresholds file found. Loading thresholds.")
            file.forEachLine {
                val split = it.split(":")
                val sensorType = split[0]
                val lowerThreshold = split[1].split(",")[0].toDoubleOrNull()
                val upperThreshold = split[1].split(",")[1].toDoubleOrNull()
                sensorThresholds[sensorType] = lowerThreshold to upperThreshold
            }
            logger.info("Sensor thresholds loaded.")
        }
    }
    override fun getUpperSensorThreshold(sensorName: String): Double? {
        return sensorThresholds[sensorName]?.first
    }

    override fun getSensorLowerThreshold(sensorName: String): Double? {
        return sensorThresholds[sensorName]?.second
    }
}