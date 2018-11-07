package com.tngtech.apicenter.backend.connector.rest.dto

import java.util.UUID

data class SessionDto(val token: String, val username: String, val userId: UUID)