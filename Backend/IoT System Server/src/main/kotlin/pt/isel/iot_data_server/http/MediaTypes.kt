package pt.isel.iot_data_server.http

import org.springframework.http.MediaType

private const val APPLICATION_TYPE = "application"
private const val SIREN_SUBTYPE = "vnd.siren+json"

val SirenMediaType = MediaType.parseMediaType("$APPLICATION_TYPE/$SIREN_SUBTYPE")

val JsonMediaType = MediaType.APPLICATION_JSON