package pt.isel.iot_data_server.http.controllers

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {
    /**
     * Uris that doesn't follow the semantic of the rest api (nouns that represent objects),
     * but rather the semantic of the application (verbs that represent actions).
     */
    object NonSemantic {
        const val logout = "logout"
        const val loggedIn = "logged-in"

        fun logout(): URI = URI(logout)
        fun loggedIn(): URI = URI(loggedIn)
    }

    object GoogleAuth {
        const val GOOGLE_AUTH = "/oidc-principal"

        fun googleAuth(): URI = URI(GOOGLE_AUTH)
    }

    object SirenInfo {
        const val SIREN_INFO = "/siren-info"
    }

    object Users {
        const val ALL = "/users"
        private const val BY_ID1 = "$ALL/{id}"
        const val ME = "$ALL/me"
        const val MY_TOKEN = "$ME/token"

        fun all(): URI = URI(ALL)
        fun create() = URI(ALL)
        fun byId(id: String) = UriTemplate(BY_ID1).expand(id)
    }

    object Devices {
        const val ALL = "/devices"
        const val BY_ID1 = ALL + "/{device_id}"
        private const val BY_ID2 = ALL + "/:device_id"
        const val BY_EMAIL = ALL + "/email/{email}"
        fun all(): URI = URI(ALL)
        fun byId(): URI = URI(BY_ID2)
        fun byId(id: String): URI = UriTemplate(BY_ID1).expand(id)

        object PH {
            const val ALL_1 = "${BY_ID1}/ph-data"
            private const val ALL_2 = "${BY_ID2}/ph"

            fun all(): URI = URI(ALL_2)
        }

        object Temperature {
            const val ALL_1 = "${BY_ID1}/temperature-data"
            private const val ALL_2 = "${BY_ID2}/temperature"

            fun all(): URI = URI(ALL_2)
        }
    }
}