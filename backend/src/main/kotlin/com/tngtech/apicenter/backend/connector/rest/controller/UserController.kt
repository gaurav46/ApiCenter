package com.tngtech.apicenter.backend.connector.rest.controller

import com.tngtech.apicenter.backend.domain.handler.UserHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController constructor(private val userHandler: UserHandler) {

    @GetMapping("/{userId}/specifications")
    fun findAllSpecificationsForUser(@PathVariable userId: UUID) = userHandler.findAllSpecificationsByUser(userId)

}