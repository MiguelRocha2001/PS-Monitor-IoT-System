import java.nio.file.Path

fun main(args: Array<String>) {
    val embeddedHiveMQBuilder = EmbeddedHiveMQ.builder()
        .withConfigurationFolder(Path.of("/path/to/embedded-config-folder"))
        .withDataFolder(Path.of("/path/to/embedded-data-folder"))
        .withExtensionsFolder(Path.of("/path/to/embedded-extensions-folder"));
}