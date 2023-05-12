package pt.isel.iot_data_server.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.iot_data_server.repository.SensorMetadataRepository

class JdbiSensorMetadataRepository(
    private val handle: Handle
) : SensorMetadataRepository {
    override fun saveSensorAlertValue(type: String, value: Double) {
        handle.createUpdate("INSERT INTO sensor (type, alert_threshold) VALUES (:type, :value)")
            .bind("type", type)
            .bind("value", value)
            .execute()
    }

    override fun getSensorAlertValue(type: String): Double? {
        return handle.createQuery("SELECT alert_threshold FROM sensor WHERE type = :type")
            .bind("type", type)
            .mapTo<Double>()
            .findFirst()
            .orElse(null)
    }

}