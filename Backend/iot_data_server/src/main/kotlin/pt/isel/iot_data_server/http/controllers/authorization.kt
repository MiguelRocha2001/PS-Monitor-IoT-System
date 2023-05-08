package pt.isel.iot_data_server.http.controllers

import pt.isel.iot_data_server.service.user.Role

annotation class Authorization(val role: Role)