package pt.isel.iot_data_server

import com.hivemq.embedded.EmbeddedHiveMQ
import com.hivemq.embedded.EmbeddedHiveMQBuilder
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Component

class HiveMQManager {

    private val embeddedHiveMQBuilder: EmbeddedHiveMQBuilder = EmbeddedHiveMQ.builder()

    private val hiveMQ: EmbeddedHiveMQ = embeddedHiveMQBuilder.build()

    @PostConstruct
    fun start() {
        try {
            hiveMQ.start().join()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @PreDestroy
    fun stop() {
        hiveMQ.stop().join()
    }
}
