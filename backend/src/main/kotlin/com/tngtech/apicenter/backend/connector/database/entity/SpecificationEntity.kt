package com.tngtech.apicenter.backend.connector.database.entity

import org.hibernate.search.annotations.Field
import org.hibernate.search.annotations.Indexed
import org.hibernate.search.annotations.IndexedEmbedded
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.OneToMany
import javax.persistence.OrderBy

@Entity
@Indexed
data class SpecificationEntity(
    @Id val id: UUID,
    @Field val title: String,
    @Field @Column(columnDefinition = "TEXT") val description: String?,
    @IndexedEmbedded @OneToMany(
        mappedBy = "specification",
        cascade = [CascadeType.ALL]
    ) @OrderBy("created DESC") val versions: List<VersionEntity>,
    val remoteAddress: String?,
    @ManyToMany @JoinTable(
        name = "SPECIFICATION_ENTITY_USERS",
        joinColumns = [JoinColumn(name = "specification_entity_id")],
        inverseJoinColumns = [JoinColumn(name = "user_entity_id")]
    ) val users: List<UserEntity>
)