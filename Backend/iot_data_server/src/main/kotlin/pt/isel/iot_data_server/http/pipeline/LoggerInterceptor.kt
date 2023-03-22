package pt.isel.iot_data_server.http.pipeline

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class LoggerInterceptor : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod) {
            logger.info("Request: ${request.method} ${request.requestURL} ${handler.method.name}")
        }
        return true
    }

    companion object {
        private val logger = LoggerFactory.getLogger(LoggerInterceptor::class.java)
    }
}