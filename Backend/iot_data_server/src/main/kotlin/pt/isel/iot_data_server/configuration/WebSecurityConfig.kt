/*
package pt.isel.iot_data_server.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

/**
 * Configures [HttpSecurity] for this application.
 */
@Configuration
class WebSecurityConfig {
    @Bean
    @Throws(Exception::class)
    protected fun configure(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors()
            .and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests { configurer ->
                configurer
                    .requestMatchers("/error")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            } // Enable JWT Authentication
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer<HttpSecurity>::jwt)
            .build()
    }
}

 */