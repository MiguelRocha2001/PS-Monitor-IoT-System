package pt.isel.iot_data_server.http.controllers

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {
    object Users {
        const val ALL = "/users"
        const val TOKEN = "/users/token"
        const val BY_ID1 = "/users/{id}"
        private const val BY_ID2 = "/users/:id"

        fun all(): URI = URI(ALL)
        fun create() = URI(ALL)
        fun byId() = URI(BY_ID2)
        fun byId(id: Int) = UriTemplate(BY_ID1).expand(id)
        fun createToken(): URI = URI(TOKEN)
        fun token(): URI = URI(TOKEN)
    }
}