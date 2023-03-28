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
        fun all(): URI = URI(ALL)

        object PH {
            const val ALL = "/devices/{id}/ph"
            const val BY_ID1 = ALL + "/{id}"

            fun all(): URI = URI(ALL)
            fun byId(id: Int): URI = UriTemplate(BY_ID1).expand(id)

        }

        object Temperature {
            const val ALL = "/devices/{id}/temperature"
            const val BY_ID1 = ALL + "/{id}"

            fun all(): URI = URI(ALL)
            fun byId(id: Int): URI = UriTemplate(BY_ID1).expand(id)

        }
    }
}