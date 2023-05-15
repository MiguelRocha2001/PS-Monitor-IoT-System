package pt.isel.iot_data_server.configuration

import pt.isel.iot_data_server.domain.SensorInfo
import java.io.File
import java.util.logging.Logger

class SensorInfoFromFile: SensorInfo {
    private val logger = Logger.getLogger(SensorInfoFromFile::class.java.name)
    private val file = File("sensor_thresholds.txt")
    private val sensorThresholds = mutableMapOf<String, Double>()
    init {
        if (!file.exists()) {
            logger.info("Sensor thresholds file not found. Creating new file.")
            file.createNewFile()
            logger.info("File created. Please fill it with the sensor thresholds.")
        } else {
            logger.info("Sensor thresholds file found. Loading thresholds.")
            file.forEachLine {
                val split = it.split(",")
                val sensorName = split[0]
                val threshold = split[1].toDouble()
                sensorThresholds[sensorName] = threshold
            }
            logger.info("Sensor thresholds loaded.")
        }
    }
    override fun getSensorThreshold(sensorName: String): Double? {
        return sensorThresholds[sensorName]
    }
}