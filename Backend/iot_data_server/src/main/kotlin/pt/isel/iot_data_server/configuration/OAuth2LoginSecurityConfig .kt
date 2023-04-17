package pt.isel.iot_data_server.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component
import pt.isel.iot_data_server.http.controllers.Uris


// See: https://www.baeldung.com/spring-security-openid-connect

@Configuration
class OAuth2LoginSecurityConfig {
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf().disable() // TODO: review this later

        val googleScopes: MutableSet<String> = HashSet()
        googleScopes.add("https://www.googleapis.com/auth/userinfo.email")
        googleScopes.add("https://www.googleapis.com/auth/userinfo.profile")
        googleScopes.add("https://www.googleapis.com/auth/userinfo.openid")
        val googleUserService = OidcUserService()
        googleUserService.setAccessibleScopes(googleScopes)

        http.authorizeHttpRequests { authorizeRequests ->
            authorizeRequests
                .requestMatchers(Uris.GoogleAuth.GOOGLE_AUTH).authenticated()
                .and().oauth2ResourceServer().jwt()
            authorizeRequests.anyRequest().permitAll()
        }
            .oauth2Login { oauthLogin: OAuth2LoginConfigurer<HttpSecurity?> ->
                oauthLogin.userInfoEndpoint()
                    .oidcUserService(googleUserService)
            }
            .logout { logout ->
                logout.logoutSuccessHandler(oidcLogoutSuccessHandler())
            }
        return http.build()
    }

    @Autowired
    private val clientRegistrationRepository: ClientRegistrationRepository? = null
    private fun oidcLogoutSuccessHandler(): LogoutSuccessHandler {
        val oidcLogoutSuccessHandler = OidcClientInitiatedLogoutSuccessHandler(
            clientRegistrationRepository
        )
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("http://localhost:8081/home")
        return oidcLogoutSuccessHandler
    }

    @Bean
    fun jwtValidator(): OAuth2TokenValidator<Jwt> {
        return DelegatingOAuth2TokenValidator(
            JwtValidators.createDefault(),
            CustomJwtValidator()
        )
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val jwtDecoder = NimbusJwtDecoder.withJwkSetUri(
            "https://www.googleapis.com/oauth2/v3/certs"
        ).build()
        jwtDecoder.setJwtValidator(jwtValidator())
        return jwtDecoder
    }

    @Component
    class CustomJwtValidator : OAuth2TokenValidator<Jwt> {
        private val log = org.slf4j.LoggerFactory.getLogger(OAuth2LoginSecurityConfig::class.java)

        override fun validate(jwt: Jwt): OAuth2TokenValidatorResult {
            // Log the Jwt object
            log.debug("Decoded Jwt: {}", jwt)

            // Add custom validation logic here

            return OAuth2TokenValidatorResult.success()
        }
    }
}