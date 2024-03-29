package pt.isel.iot_data_server.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.iot_data_server.http.pipeline.AuthInterceptor
import pt.isel.iot_data_server.http.pipeline.LoggerInterceptor
import pt.isel.iot_data_server.http.pipeline.UserArgumentResolver


@Configuration
class PipelineConfigurer(
    val authInterceptor: AuthInterceptor,
    val userArgumentResolver: UserArgumentResolver,
    val loggerInterceptor: LoggerInterceptor,
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:8080", "http://localhost:9000") // TODO: review this
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowCredentials(true)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authInterceptor)
        registry.addInterceptor(loggerInterceptor)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(userArgumentResolver)
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(0) // Optional: Set a cache period in seconds
    }
}