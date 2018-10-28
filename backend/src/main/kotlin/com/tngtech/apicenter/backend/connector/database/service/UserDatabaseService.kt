package com.tngtech.apicenter.backend.connector.database.service

import com.tngtech.apicenter.backend.connector.database.mapper.SpecificationEntityMapper
import com.tngtech.apicenter.backend.connector.database.mapper.UserEntityMapper
import com.tngtech.apicenter.backend.connector.database.repository.UserRepository
import com.tngtech.apicenter.backend.domain.entity.User
import com.tngtech.apicenter.backend.domain.service.UserPersistenceService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserDatabaseService constructor(
    private val userRepository: UserRepository,
    private val userEntityMapper: UserEntityMapper,
    private val specificationEntityMapper: SpecificationEntityMapper
) : UserPersistenceService {

    override fun save(user: User) {
        userRepository.save(userEntityMapper.fromDomain(user))
    }

    override fun findByOrigin(origin: String, externalId: String) =
        userRepository.findByOriginAndExternalId(origin, externalId)

    override fun exists(origin: String, externalId: String) = userRepository.checkExistenceByOrigin(origin, externalId)

    override fun findAllSpecificationsByUser(userId: UUID) =
        userRepository.findById(userId).get().specifications.map { specificationEntityMapper.toDomain(it) }
}