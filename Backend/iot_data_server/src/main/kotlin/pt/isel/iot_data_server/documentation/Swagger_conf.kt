package pt.isel.iot_data_server.documentation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
class SwaggerConfig {


    @Bean
    fun docket(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("pt.isel.iot_data_server"))
                .paths(PathSelectors.any())
                .build()
    }




}