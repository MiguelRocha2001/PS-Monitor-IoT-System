package hive

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.InputStream
import java.security.KeyStore
import javax.net.SocketFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

class MqttClient {
    companion object {
        fun getMqttClient(): MqttClient =
            MqttClient("tcp://localhost:1883", MqttClient.generateClientId())
    }
}

class TlsMqttClient {
    companion object {
        private val log = LoggerFactory.getLogger(TlsMqttClient::class.java)

        @Throws(Exception::class)
        fun getTruststoreFactory(): SocketFactory {
            val trustStore = KeyStore.getInstance("JKS")
            val `in`: InputStream = FileInputStream("conf/mqtt-client-trust-store.jks")
            trustStore.load(`in`, "changeme".toCharArray())
            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            tmf.init(trustStore)
            val sslCtx: SSLContext = SSLContext.getInstance("TLSv1.2")
            sslCtx.init(null, tmf.trustManagers, null)
            return sslCtx.socketFactory
        }
        fun getTlsMqttClient(): MqttClient {
            val clientId = "sslTestClient"
            val client = MqttClient("ssl://localhost:8883", clientId, MemoryPersistence())
            val mqttConnectOptions = MqttConnectOptions()
            try {
                mqttConnectOptions.socketFactory = getTruststoreFactory()
            } catch (e: Exception) {
                log.error("Error while setting up TLS", e)
            }
            return client
        }
    }
}
