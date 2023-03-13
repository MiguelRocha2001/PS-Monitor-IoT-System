import com.hivemq.embedded.EmbeddedHiveMQ
import com.hivemq.embedded.EmbeddedHiveMQBuilder
import org.springframework.stereotype.Component

@Component
class HiveMQManager {

    private val embeddedHiveMQBuilder: EmbeddedHiveMQBuilder = EmbeddedHiveMQ.builder()

    private val hiveMQ: EmbeddedHiveMQ = embeddedHiveMQBuilder.build()

    fun start() {
        try {
            hiveMQ.start().join()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun stop() {
        hiveMQ.stop().join()
    }
}
