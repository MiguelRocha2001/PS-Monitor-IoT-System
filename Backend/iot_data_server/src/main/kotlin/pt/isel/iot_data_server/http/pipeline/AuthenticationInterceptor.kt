package pt.isel.iot_data_server.http.pipeline

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.iot_data_server.domain.User
import pt.isel.iot_data_server.service.user.UserService

@Component
class AuthenticationInterceptor(
    val service: UserService
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod && handler.methodParameters.any { it.parameterType == User::class.java }) {
            // enforce authentication
            val token = request.cookies?.find { it.name == "token" }?.value
            if (token != null) {
                val user = service.getUserByToken(token)
                if (user != null) {
                    UserArgumentResolver.addUserTo(user, request)
                    return true.also { logger.info("Request: ${request.method} ${request.requestURI} - Authorized") }
                }
            }
            response.status = 401
            // response.addHeader(NAME_WWW_AUTHENTICATE_HEADER, AuthorizationHeaderProcessor.SCHEME)
            return false.also { logger.info("Request: ${request.method} ${request.requestURI} - Unauthorized") }
        }

        return true
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthenticationInterceptor::class.java)
        private const val NAME_AUTHORIZATION_HEADER = "Authorization"
        private const val NAME_WWW_AUTHENTICATE_HEADER = "WWW-Authenticate"
    }
}