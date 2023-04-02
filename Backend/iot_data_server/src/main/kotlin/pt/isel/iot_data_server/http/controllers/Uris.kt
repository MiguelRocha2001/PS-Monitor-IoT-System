package pt.isel.iot_data_server.http.controllers

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {
    object Users {
        const val ALL = "/users"
        const val TOKEN = "/users/token"
        const val BY_ID1 = "/users/{id}"
        private const val BY_ID2 = "/users/:id"
        const val ME = "/users/me"

        object Me {
            const val loggedIn = "/users/me/loggedIn"

            fun loggedIn(): URI = URI(loggedIn)
        }

        fun all(): URI = URI(ALL)
        fun create() = URI(ALL)
        fun byId() = URI(BY_ID2)
        fun byId(id: Int) = UriTemplate(BY_ID1).expand(id)
        fun createToken(): URI = URI(TOKEN)
        fun token(): URI = URI(TOKEN)
    }

    object Devices {
        const val ALL = "/devices"
        const val BY_ID1 = ALL + "/{device_id}"
        private const val BY_ID2 = ALL + "/:device_id"
        fun all(): URI = URI(ALL)
        fun byId(): URI = URI(BY_ID2)
        fun byId(id: String): URI = UriTemplate(BY_ID1).expand(id)

        object PH {
            const val ALL_1 = "/devices/{device_id}/ph"
            const val BY_ID_1 = ALL_1 + "/{id}"

            private const val ALL_2 = "/devices/:device_id/ph"

            fun all(): URI = URI(ALL_2)
            fun byId(id: Int): URI = UriTemplate(BY_ID_1).expand(id)

        }

        object Temperature {
            const val ALL_1 = "/devices/{device_id}/temperature"
            const val BY_ID_1 = ALL_1 + "/{id}"

            private const val ALL_2 = "/devices/:device_id/temperature"

            fun all(): URI = URI(ALL_2)
            fun byId(id: Int): URI = UriTemplate(BY_ID_1).expand(id)
        }
    }
}