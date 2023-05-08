package pt.isel.iot_data_server.domain

import org.junit.jupiter.api.Test

class MQTTHandlerTests {
    @Test
    fun `From json to PhRecord`() {
        val json = "{\"value\": 7.0, \"timestamp\": 1622550000, \"device_id\": \"device1\"}"
        val phRecord = fromMqttMsgStringToPhRecord(json)
        val deviceId = fromMqttMsgStringToDeviceId(json)

        assert(phRecord.value == 7.0)
        assert(phRecord.instant.epochSecond == 1622550000L)
        assert(deviceId == "device1")
    }

    @Test
    fun `From json to TemperatureRecord`() {
        val json = "{\"value\": 7.0, \"timestamp\": 1622550000, \"device_id\": \"device1\"}"
        val temperatureRecord = fromMqttMessageToTemperatureRecord(json)
        val deviceId = fromMqttMsgStringToDeviceId(json)

        assert(temperatureRecord.value == 7.0)
        assert(temperatureRecord.instant.epochSecond == 1622550000L)
        assert(deviceId == "device1")
    }

    @Test
    fun `From json to FloodRecord`() {
        val json = "{\"timestamp\": 1622550000, \"device_id\": \"device1\"}"
        val floodRecord = fromMqttMsgStringToFloodRecord(json)
        val deviceId = fromMqttMsgStringToDeviceId(json)

        assert(floodRecord.instant.epochSecond == 1622550000L)
        assert(deviceId == "device1")
    }
}