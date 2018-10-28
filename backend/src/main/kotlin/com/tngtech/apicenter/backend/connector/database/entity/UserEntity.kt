package com.tngtech.apicenter.backend.connector.database.entity

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToMany

@Entity
data class UserEntity(
    @Id @GeneratedValue val id: UUID,
    val username: String,
    val email: String,
    val origin: String,
    val externalId: String,
    @ManyToMany val specifications: List<SpecificationEntity>)