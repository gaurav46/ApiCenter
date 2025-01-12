package com.tngtech.apicenter.backend.connector.rest.mapper.converter

import com.tngtech.apicenter.backend.connector.rest.dto.SpecificationFileDto
import com.tngtech.apicenter.backend.connector.rest.service.SpecificationDataParser
import com.tngtech.apicenter.backend.connector.rest.service.SpecificationFileDownloader
import com.tngtech.apicenter.backend.domain.entity.Specification
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.tngtech.apicenter.backend.connector.rest.dto.SpecificationFileMetadata
import com.tngtech.apicenter.backend.domain.entity.ApiLanguage
import com.tngtech.apicenter.backend.domain.entity.ServiceId
import com.tngtech.apicenter.backend.domain.entity.SpecificationMetadata
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class SpecificationFileDtoConverterTest {

    companion object {
        const val SWAGGER_SPECIFICATION =
            "{\"swagger\": \"2.0\", \"info\": {\"version\": \"1.0.0\",\"title\": \"Swagger Petstore\",\"description\":\"Description\"}}"
        const val SWAGGER_REMOTE = "https://swagger.com/remote/file.json"
        const val UUID_STRING = "5aa40ba9-7e26-44de-81ec-f545d1f178aa"
    }
    private val metadata = SpecificationMetadata(ServiceId(UUID_STRING), "Swagger Petstore", "1.0.0", "Description", ApiLanguage.OPENAPI, null)

    private val specificationDataParser: SpecificationDataParser = mock()

    private val specificationFileDownloader: SpecificationFileDownloader = mock()

    private val specificationFileDtoConverter: SpecificationFileDtoConverter =
        SpecificationFileDtoConverter(
            specificationFileDownloader,
            specificationDataParser
        )

    @Before
    fun setup() {
        given(specificationDataParser.extractTitle(SWAGGER_SPECIFICATION)).willReturn("Swagger Petstore")
        given(specificationDataParser.extractVersion(SWAGGER_SPECIFICATION)).willReturn("1.0.0")
        given(specificationDataParser.extractDescription(SWAGGER_SPECIFICATION)).willReturn("Description")
        given(specificationDataParser.parseFileContent(SWAGGER_SPECIFICATION)).willReturn(SWAGGER_SPECIFICATION)
    }

    @Test
    fun convert_shouldReturnSpecification() {
        val specificationFileDto = SpecificationFileDto(SWAGGER_SPECIFICATION, id = UUID_STRING)

        given(specificationFileDownloader.getLocalOrRemoteFileContent(specificationFileDto)).willReturn(SWAGGER_SPECIFICATION)
        given(specificationDataParser.makeSpecificationMetadata(SWAGGER_SPECIFICATION, UUID_STRING)).willReturn(metadata)

        val specification = specificationFileDtoConverter.convert(specificationFileDto, null, null)

        val expectedSpecification = Specification(SWAGGER_SPECIFICATION, metadata)

        assertThat(specification.content).isEqualTo(expectedSpecification.content)
        assertThat(specification.metadata).isEqualTo(expectedSpecification.metadata)
    }

    @Test
    fun convert_shouldReturnRemoteSpecification() {
        val specificationFileDto = SpecificationFileDto(null,
            SWAGGER_REMOTE,
            id = UUID_STRING
        )

        given(specificationFileDownloader.retrieveFile(SWAGGER_REMOTE)).willReturn(SWAGGER_SPECIFICATION)
        given(specificationFileDownloader.getLocalOrRemoteFileContent(specificationFileDto)).willReturn(SWAGGER_SPECIFICATION)

        given(specificationDataParser.makeSpecificationMetadata(fileContent = SWAGGER_SPECIFICATION, idFromPath = UUID_STRING)).willReturn(metadata)

        val specification = specificationFileDtoConverter.convert(specificationFileDto, null, null)

        val expectedSpecification = Specification(SWAGGER_SPECIFICATION, metadata)

        assertThat(specification.content).isEqualTo(expectedSpecification.content)
        assertThat(specification.metadata).isEqualTo(expectedSpecification.metadata)
    }
}