package pt.isel.iot_data_server.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler


@Configuration
class OAuth2LoginSecurityConfig {
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val googleScopes: MutableSet<String> = HashSet()
        googleScopes.add("https://www.googleapis.com/auth/userinfo.email")
        googleScopes.add("https://www.googleapis.com/auth/userinfo.profile")
        val googleUserService = OidcUserService()
        googleUserService.setAccessibleScopes(googleScopes)
        http.authorizeHttpRequests { authorizeRequests ->
            authorizeRequests.anyRequest()
                .anonymous()
                //.authenticated()
        }
            /*
            .oauth2Login { oauthLogin: OAuth2LoginConfigurer<HttpSecurity?> ->
                oauthLogin.userInfoEndpoint()
                    .oidcUserService(googleUserService)
            }
            .logout { logout ->
                logout.logoutSuccessHandler(oidcLogoutSuccessHandler())
            }
             */
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
}