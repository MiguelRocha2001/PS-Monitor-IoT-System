package pt.isel.iot_data_server.configuration

import java.io.File
import java.util.logging.Logger

class NeutralizationDeviceStabilizationTime {
    private val logger = Logger.getLogger(SensorInfoFromFile::class.java.name)
    private val file = File("neutralization-device-stabilization-time.txt")
    val time: Long

    init {
        if (!file.exists()) {
            logger.info("Neutralization device stabilization time file not found. Creating new file.")
            file.createNewFile()
            logger.info("File created. Please fill it with the stabilization time in seconds.")
            throw IllegalStateException("Neutralization device stabilization time file not found.")
        } else {
            logger.info("Neutralization device stabilization time file found. Loading stabilization time.")
            time = file.readText().toLong()
            logger.info("Neutralization device stabilization time loaded.")
        }
    }
}