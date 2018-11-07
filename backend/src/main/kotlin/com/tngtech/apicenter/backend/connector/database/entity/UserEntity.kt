package com.tngtech.apicenter.backend.connector.database.entity

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany

@Entity
data class UserEntity(
    @Id @GeneratedValue val id: UUID,
    val username: String,
    val email: String,
    val origin: String,
    val externalId: String,
    @ManyToMany @JoinTable(
        name = "SPECIFICATION_ENTITY_USERS",
        joinColumns = [JoinColumn(name = "user_entity_id")],
        inverseJoinColumns = [JoinColumn(name = "specification_entity_id")]
    ) val specifications: List<SpecificationEntity>
)