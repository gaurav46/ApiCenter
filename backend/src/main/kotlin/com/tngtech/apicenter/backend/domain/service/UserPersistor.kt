package com.tngtech.apicenter.backend.domain.service

import com.tngtech.apicenter.backend.domain.entity.User

interface UserPersistor {
    fun save(user: User)
    fun exists(origin: String, externalId: String): Boolean
    fun findByOrigin(origin: String, externalId: String): User
}