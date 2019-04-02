package com.tngtech.apicenter.backend.connector.rest.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.tngtech.apicenter.backend.domain.exceptions.SpecificationNotFoundException
import com.tngtech.apicenter.backend.domain.entity.Version
import com.tngtech.apicenter.backend.connector.rest.dto.VersionDto
import com.tngtech.apicenter.backend.connector.rest.mapper.VersionDtoMapper
import com.tngtech.apicenter.backend.domain.service.VersionPersistenceService
import org.springframework.web.bind.annotation.*
import org.springframework.http.MediaType
import com.tngtech.apicenter.backend.domain.entity.ServiceId

private const val MEDIA_TYPE_YAML = "application/yml"

@RestController
class VersionController constructor(private val versionPersistenceService: VersionPersistenceService, private val versionDtoMapper: VersionDtoMapper) {

    @RequestMapping("/api/v1/specifications/{specificationId}/versions/{version}",
            produces = [MediaType.APPLICATION_JSON_VALUE,
                        MEDIA_TYPE_YAML],
            headers =  ["Accept=" + MediaType.APPLICATION_JSON_VALUE,
                        "Accept=" + MEDIA_TYPE_YAML],
            method =   [RequestMethod.GET])
    fun findVersion(@PathVariable specificationId: String,
                    @PathVariable version: String,
                    @RequestHeader(value = "Accept",
                                   defaultValue = MediaType.APPLICATION_JSON_VALUE) accept: String = MediaType.APPLICATION_JSON_VALUE): VersionDto {
        // i.e. The integration test and unit test require the default specified in two different ways
        val foundVersion = versionPersistenceService.findOne(ServiceId(specificationId), version)
                ?: throw SpecificationNotFoundException(specificationId, version)
        return versionDtoMapper.fromDomain(convertVersion(accept, foundVersion))
    }

    private fun convertVersion(accept: String, foundVersion: Version): Version {
        return if (accept == MEDIA_TYPE_YAML) {
            val jsonNodeTree = ObjectMapper().readTree(foundVersion.content)
            val jsonAsYaml = YAMLMapper().writeValueAsString(jsonNodeTree)
            Version(jsonAsYaml, foundVersion.metadata)
        } else {
            foundVersion
        }
    }

    @DeleteMapping("/api/v1/specifications/{specificationId}/versions/{version}")
    fun deleteVersion(@PathVariable specificationId: String, @PathVariable version: String) {
        versionPersistenceService.delete(ServiceId(specificationId), version)
    }
}
