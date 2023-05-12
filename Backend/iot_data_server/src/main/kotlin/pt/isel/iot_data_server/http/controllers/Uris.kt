package pt.isel.iot_data_server.http.controllers

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {
    /**
     * Uris that doesn't follow the semantic of the rest api (nouns that represent objects),
     * but rather the semantic of the application (verbs that represent actions).
     */
    object NonSemantic {
        // logout is a reserved word when using HttpSecurity
        // @see https://docs.spring.io/spring-security/reference/servlet/authentication/logout.html
        const val logout = "log-out"
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

    object Data {
        const val ALL = "/all"
    }

    object Users {
        const val ALL = "/users"
        const val BY_ID1 = "$ALL/{id}"
        const val ME = "$ALL/me"
        const val MY_TOKEN = "$ME/token"

        fun all(): URI = URI(ALL)
        fun create() = URI(ALL)
        fun byId(id: String) = UriTemplate(BY_ID1).expand(id)
    }

    object Devices {
        const val ALL = "/devices"
        const val COUNT = "$ALL/count"
        const val FILTER = "$ALL/filter"

        const val BY_ID1 = ALL + "/{device_id}"
        private const val BY_ID2 = ALL + "/:device_id"
        const val BY_EMAIL = ALL + "/email/{email}"
        const val BY_WORD = FILTER + "/{word}"

        object My {
            const val ALL = "/my${Devices.ALL}"
            const val COUNT = "/my${Devices.COUNT}"

            const val BY_ID1 = "/my${Devices.BY_ID1}"
            const val BY_ID2 = "/my${Devices.BY_ID2}"

            fun all(): URI = URI(ALL)
            fun byId(id: String): URI = UriTemplate(BY_ID1).expand(id)
        }

        fun all(): URI = URI(ALL)
        fun byId(): URI = URI(BY_ID2)
        fun byId(id: String): URI = UriTemplate(BY_ID1).expand(id)

        object Sensor {
            const val ALL_1 = "${BY_ID1}/sensors"
            const val ALL_2 = "${BY_ID2}/sensors"
            const val NAMES = "${ALL_2}/names"
            fun all(): URI = URI(ALL_2)
        }

        object PH {
            const val ALL_1 = "${BY_ID1}/ph-data"
            private const val ALL_2 = "${BY_ID2}/ph-data"

            fun all(): URI = URI(ALL_2)
        }

        object Temperature {
            const val ALL_1 = "${BY_ID1}/temperature-data"
            private const val ALL_2 = "${BY_ID2}/temperature-data"

            fun all(): URI = URI(ALL_2)
        }

        object WaterFlow {
            const val ALL_1 = "${BY_ID1}/water-flow-data"
            private const val ALL_2 = "${BY_ID2}/water-flow-data"

            fun all(): URI = URI(ALL_2)
        }

        object WaterLevel {
            const val ALL_1 = "${BY_ID1}/water-level-data"
            private const val ALL_2 = "${BY_ID2}/water-level-data"

            fun all(): URI = URI(ALL_2)
        }

        object Humidity {
            const val ALL_1 = "${BY_ID1}/humidity-data"
            private const val ALL_2 = "${BY_ID2}/humidity-data"

            fun all(): URI = URI(ALL_2)
        }

        object Error {
            const val ALL_1 = "${BY_ID1}/error-data"
            private const val ALL_2 = "${BY_ID2}/error-data"

            fun all(): URI = URI(ALL_2)
        }

        object SensorError {
            const val ALL_1 = "${BY_ID1}/sensor-error-data"
            private const val ALL_2 = "${BY_ID2}/sensor-error-data"

            fun all(): URI = URI(ALL_2)
        }
    }
}