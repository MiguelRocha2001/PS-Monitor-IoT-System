package hive

import com.hivemq.embedded.EmbeddedHiveMQ
import com.hivemq.embedded.EmbeddedHiveMQBuilder
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import java.nio.file.Path

class HiveMQManager {

    private val embeddedHiveMQBuilder: EmbeddedHiveMQBuilder = EmbeddedHiveMQ.builder()
    private val hiveMQ: EmbeddedHiveMQ = embeddedHiveMQBuilder
        // .withConfigurationFolder(Path.of("src/main/resources/hivemq/conf")) // TODO: uncomment later
        .build()

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
