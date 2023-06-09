package pt.isel.iot_data_server.http.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.iot_data_server.http.model.Problem
import pt.isel.iot_data_server.service.Either
import pt.isel.iot_data_server.service.user.Role
import pt.isel.iot_data_server.service.user.UserService

/*
@RestController
class GoogleAuthController(
    val service: UserService
) {
    @Operation(summary = "Login with Google", description = "Login with Google")
    @ApiResponse(responseCode = "204", description = "Successfully logged")
    @ApiResponse(responseCode = "403", description = "User Or Password Are Invalid", content = [Content(
        mediaType = "application/problem+json",
        schema = Schema(implementation = Problem::class)
    )])
    @ApiResponse(responseCode = "409", description = "User or email already exist",
        content = [Content(
            mediaType = "application/problem+json",
            schema = Schema(implementation = Problem::class)
        )])
    @GetMapping(Uris.GoogleAuth.GOOGLE_AUTH) // FIXE: dont know why but the URI needs to be this one
    fun loginWithGoogleAuth(
        @AuthenticationPrincipal oidcUser: OidcUser?,
        response: HttpServletResponse
    ) {
        val email = oidcUser?.userInfo?.email ?: throw RuntimeException("Error getting user info")

        val user = service.getUserByEmail(email)
        if (user == null) {
            val userCreationResult = service.createUser(email, null, Role.USER) // password is not needed
            if (userCreationResult is Either.Left) {
                throw RuntimeException("Error creating user")
            }
        }

        val res = service.createAndGetToken(email, null) // password is not needed

        // adds cookie to response
        if (res is Either.Right) {
            val cookie = buildCookie(60 * 60 * 24 * 7, res.value)
            response.addCookie(cookie)
        }

        response.sendRedirect("/home") // FIXME
    }

    private fun buildCookie(maxAge: Int, value: String?): Cookie {
        val cookieWithToken = Cookie("token", value ?: "null")
        cookieWithToken.path = "/"
        cookieWithToken.isHttpOnly = true
        cookieWithToken.secure = true
        cookieWithToken.maxAge = maxAge
        return cookieWithToken
    }
}

 */