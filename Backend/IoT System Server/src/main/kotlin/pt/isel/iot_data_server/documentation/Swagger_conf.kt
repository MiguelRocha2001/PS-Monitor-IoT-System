package pt.isel.iot_data_server.documentation
/*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.PathSelectors

@Configuration
@EnableSwagger2
class SwaggerConfig {
    @Bean
    fun docket(): Docket {
        val port = 8888 // Hardcoded port number

        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
            .host("localhost:$port")
    }
}*/