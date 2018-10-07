package com.tngtech.apicenter.backend.connector.rest.mapper.converter

import com.tngtech.apicenter.backend.connector.rest.dto.SpecificationFileDto
import com.tngtech.apicenter.backend.connector.rest.service.SpecificationDataService
import com.tngtech.apicenter.backend.connector.rest.service.SpecificationFileService
import com.tngtech.apicenter.backend.domain.entity.Specification
import com.tngtech.apicenter.backend.domain.entity.Version
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.UUID

class SpecificationConverterTest {

    companion object {
        const val SWAGGER_SPECIFICATION =
            "{\"swagger\": \"2.0\", \"info\": {\"version\": \"1.0.0\",\"title\": \"Swagger Petstore\",\"description\":\"Description\"}}"
        const val SWAGGER_REMOTE = "https://swagger.com/remote/file.json"
        const val UUID_STRING = "5aa40ba9-7e26-44de-81ec-f545d1f178aa"
    }

    private val specificationDataService: SpecificationDataService = mock()

    private val specificationFileService: SpecificationFileService = mock()

    private val specificationConverter: SpecificationConverter =
        SpecificationConverter(
            specificationFileService,
            specificationDataService
        )

    @Test
    fun convert_shouldReturnSpecification() {
        val specificationFileDto = SpecificationFileDto(SWAGGER_SPECIFICATION)

        given(specificationDataService.parseFileContent(SWAGGER_SPECIFICATION)).willReturn(
            SWAGGER_SPECIFICATION
        )
        given(specificationDataService.readTitle(SWAGGER_SPECIFICATION)).willReturn("Swagger Petstore")
        given(specificationDataService.readVersion(SWAGGER_SPECIFICATION)).willReturn("1.0.0")

        val specification = specificationConverter.convert(specificationFileDto, null, null)

        val expectedSpecification = Specification(
            specification.id,
            "Swagger Petstore",
            "Description",
            listOf(Version(specification.versions[0].id, "1.0.0", SWAGGER_SPECIFICATION)),
            ""
        )

        assertThat(specification.id).isEqualTo(expectedSpecification.id)
        assertThat(specification.title).isEqualTo(expectedSpecification.title)
        assertThat(specification.versions).isEqualTo(expectedSpecification.versions)
        assertThat(specification.remoteAddress).isEqualTo(expectedSpecification.remoteAddress)
    }

    @Test
    fun convert_shouldReturnRemoteSpecification() {
        val specificationFileDto = SpecificationFileDto(null,
            SWAGGER_REMOTE
        )

        given(specificationFileService.retrieveFile(SWAGGER_REMOTE)).willReturn(
            SWAGGER_SPECIFICATION
        )
        given(specificationDataService.parseFileContent(SWAGGER_SPECIFICATION)).willReturn(
            SWAGGER_SPECIFICATION
        )
        given(specificationDataService.readTitle(SWAGGER_SPECIFICATION)).willReturn("Swagger Petstore")
        given(specificationDataService.readVersion(SWAGGER_SPECIFICATION)).willReturn("1.0.0")

        val specification = specificationConverter.convert(specificationFileDto, null, null)

        val expectedSpecification = Specification(
            specification.id,
            "Swagger Petstore",
            "Description",
            listOf(Version(specification.versions[0].id, "1.0.0", SWAGGER_SPECIFICATION)),
            SWAGGER_REMOTE
        )

        assertThat(specification.id).isEqualTo(expectedSpecification.id)
        assertThat(specification.title).isEqualTo(expectedSpecification.title)
        assertThat(specification.versions).isEqualTo(expectedSpecification.versions)
        assertThat(specification.remoteAddress).isEqualTo(expectedSpecification.remoteAddress)
    }
}