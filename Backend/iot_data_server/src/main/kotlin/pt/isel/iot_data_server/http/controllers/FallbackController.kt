package pt.isel.iot_data_server.http.controllers

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


/**
 * Defines the fallback controller for the application.
 * Change application.properties to redirect to this controller on error.
 */
@RestController
@RequestMapping("/my-error")
class FallbackController: ErrorController {
    @RequestMapping
    fun fallback(
        response: HttpServletResponse
    ): String { // TODO: change to a better fallback
        return "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1,shrink-to-fit=no\"><meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\"><link rel=\"preconnect\" href=\"https://fonts.googleapis.com\"><link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin><link href=\"https://fonts.googleapis.com/css2?family=Roboto+Mono:wght@300&display=swap\" rel=\"stylesheet\"><link rel=\"icon\" href=\"../src/views/logo_page.png\" type=\"image/x-icon\"><title>Iot Monitoring</title><script src=\"/main.js\" type=\"module\"></script><script defer=\"defer\" src=\"main.js\"></script></head><body><main style=\"width: 100%; height:100%\"><div class=\"container\" id=\"root\"></div></main></body></html>"
    }
}

