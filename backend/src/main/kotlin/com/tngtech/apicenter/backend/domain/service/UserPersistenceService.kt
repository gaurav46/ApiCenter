package com.tngtech.apicenter.backend.domain.service

import com.tngtech.apicenter.backend.domain.entity.Specification
import com.tngtech.apicenter.backend.domain.entity.User
import java.util.UUID

interface UserPersistenceService {
    fun save(user: User)
    fun exists(origin: String, externalId: String): Boolean
    fun findByOrigin(origin: String, externalId: String): User
    fun findAllSpecificationsByUser(userId: UUID): List<Specification>
}