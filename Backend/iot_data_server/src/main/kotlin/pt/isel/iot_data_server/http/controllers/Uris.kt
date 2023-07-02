package pt.isel.iot_data_server.http.controllers

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {
    const val API = "/api"

    /**
     * Uris that doesn't follow the semantic of the rest api (nouns that represent objects),
     * but rather the semantic of the application (verbs that represent actions).
     */
    object NonSemantic {
        // logout is a reserved word when using HttpSecurity
        // @see https://docs.spring.io/spring-security/reference/servlet/authentication/logout.html
        const val logout = "/log-out"
        const val loggedIn = "/logged-in"

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
        const val BY_ID1 = "$ALL/{userId}"
        const val BY_ID2 = "$ALL/:userId"
        const val ME = "$ALL/me"
        const val MY_TOKEN = "$ME/token"
        const val COUNT = "$ALL/count"

        fun all(): URI = URI(ALL)
        fun create() = URI(ALL)
        fun byId(id: String) = UriTemplate(BY_ID1).expand(id)

        object exists {
            const val BY_EMAIL_1 = "$ALL/exists/email/{email}"
            const val BY_EMAIL_2 = "$ALL/exists/email/:email"

            fun byEmail(email: String) = UriTemplate(BY_EMAIL_2).expand(email)
        }

        object Devices {
            const val ALL = "/devices"
            const val ALL_1 = "${Users.BY_ID1}/devices"
            const val ALL_2 = "${Users.BY_ID2}/devices"
            const val COUNT_1 = "$ALL_1/count"
            const val COUNT_2 = "$ALL_2/count"
            const val FILTER_1 = "$ALL_1/filter"
            const val FILTER_2 = "$ALL_2/filter"

            const val BY_ID1 = "/devices/{device_id}"
            const val BY_ID2 = "/devices/:device_id"
            const val BY_EMAIL_1 = ALL_1 + "/email/{email}"
            const val BY_EMAIL_2 = ALL_2 + "/email/:email"
            const val BY_WORD_1 = FILTER_1 + "/{word}"
            const val BY_WORD_2 = FILTER_2 + "/:word"
            const val COUNT_FILTERED_1 = BY_WORD_1 + "/count"
            const val COUNT_FILTERED_2 = BY_WORD_2 + "/count"

            fun allByUser() = URI(ALL_2)
            fun all(): URI = URI(ALL)
            fun byId(): URI = URI(BY_ID2)
            fun byId(id: String): URI = UriTemplate(BY_ID1).expand(id)

            object Sensor {
                const val ALL_1 = "${BY_ID1}/sensors"
                const val ALL_2 = "${BY_ID2}/sensors"
                const val TYPES_1 = "${ALL_1}/types"
                const val TYPES_2 = "${ALL_2}/types"
                fun all(): URI = URI(ALL_2)
            }

            object WakeUpLogs {
                const val ALL_1 = "${BY_ID1}/wake-up-logs"
                private const val ALL_2 = "${BY_ID2}/wake-up-logs"

                fun all(): URI = URI(ALL_2)
            }

            object SensorError {
                const val ALL_1 = "${BY_ID1}/sensor-error-data"
                private const val ALL_2 = "${BY_ID2}/sensor-error-data"

                fun all(): URI = URI(ALL_2)
            }
        }
    }

    object Verification {
        const val ALL = "/verification"
        const val CODE = "$ALL/code"
        const val GENERATE = "$ALL/generate"
    }
}