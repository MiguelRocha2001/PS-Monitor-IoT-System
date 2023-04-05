package pt.isel.iot_data_server.repository.tsdb

interface TSDBConfigProperties {
    val token: String
    val org: String
    val bucket: String
    val path: String
}