package pt.isel.iot_data_server.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.iot_data_server.http.pipeline.AuthenticationInterceptor
import pt.isel.iot_data_server.http.pipeline.LoggerInterceptor

@Configuration
class PipelineConfigurer(
    val authenticationInterceptor: AuthenticationInterceptor,
    val loggerInterceptor: LoggerInterceptor
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowCredentials(true)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authenticationInterceptor)
        registry.addInterceptor(loggerInterceptor)
    }
}