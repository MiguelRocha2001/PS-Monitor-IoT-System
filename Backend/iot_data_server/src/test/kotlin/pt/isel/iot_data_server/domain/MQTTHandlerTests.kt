package pt.isel.iot_data_server.domain

import org.junit.jupiter.api.Test

class MQTTHandlerTests {
    @Test
    fun `From json to PhRecord`() {
        val json =
            "{\"value\": 7.0, \"timestamp\": 1622550000, \"device_id\": \"device1\", \"sensor_type\": \"initial ph\"}"
        val phRecord = fromMqttMsgStringToSensorRecord(json)
        val deviceId = fromMqttMsgStringToDeviceId(json)

        assert(phRecord.value == 7.0)
        assert(phRecord.instant.epochSecond == 1622550000L)
        assert(deviceId == "device1")
    }


    @Test
    fun `From json to TemperatureRecord`() {
        val json =
            "{\"value\": 7.0, \"timestamp\": 1622550000, \"device_id\": \"device1\", \"sensor_type\": \"temperature\"}"
        val temperatureRecord = fromMqttMsgStringToSensorRecord(json)
        val deviceId = fromMqttMsgStringToDeviceId(json)

        assert(temperatureRecord.value == 7.0)
        assert(temperatureRecord.instant.epochSecond == 1622550000L)
        assert(deviceId == "device1")
    }


    @Test
    fun `From json to FloodRecord`() {
        val json = "{\"value\": 1.0,\"timestamp\": 1622550000, \"device_id\": \"device1\", \"sensor_type\": \"floodRecord\"}"
        val floodRecord = fromMqttMsgStringToSensorRecord(json)
        val deviceId = fromMqttMsgStringToDeviceId(json)

        assert(floodRecord.instant.epochSecond == 1622550000L)
        assert(deviceId == "device1")
    }
}

