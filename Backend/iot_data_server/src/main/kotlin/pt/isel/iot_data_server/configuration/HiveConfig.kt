package pt.isel.iot_data_server.configuration

import HiveMQManager
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

@Configuration
class HiveConfig {

    @Autowired
    lateinit var hiveMQManager: HiveMQManager

    @PostConstruct
    fun init() {
        hiveMQManager.start()
    }

    @PreDestroy
    fun destroy() {
        hiveMQManager.stop()
    }
}