package com.tngtech.apicenter.backend.connector.rest.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.tngtech.apicenter.backend.domain.exceptions.SpecificationParseException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import java.io.IOException

class SpecificationDataParserTest {

    private val objectMapper: ObjectMapper = mock()

    private val yamlMapper: YAMLMapper = mock()

    private val specificationDataParser = SpecificationDataParser(objectMapper, yamlMapper)

    companion object {
        const val SWAGGER_SPECIFICATION =
            "{\"swagger\": \"2.0\", \"info\": {\"version\": \"1.0.0\",\"title\": \"Swagger Petstore\",\"description\": \"My Description\"}}"
        const val SWAGGER_SPECIFICATION_WITHOUT_TITLE = "{\"swagger\": \"2.0\", \"info\": {\"version\": \"1.0.0\"}}"
        const val SWAGGER_SPECIFICATION_WITHOUT_VERSION =
            "{\"swagger\": \"2.0\", \"info\": {\"title\": \"Swagger Petstore\"}}"
        const val SWAGGER_SPECIFICATION_WITHOUT_DESCRIPTION =
            "{\"swagger\": \"2.0\", \"info\": {\"title\": \"Swagger Petstore\",\"version\": \"1.0.0\"}}"
        const val YAML_SPECIFICATION = "openapi: \"3.0.0\"\r\ninfo:\r\n  version: 1.0.0\r\n  title: Swagger Petstore"
    }

    @Test
    fun parseFileContent_shouldReturnFileContent() {
        assertThat(specificationDataParser.parseFileContent(SWAGGER_SPECIFICATION)).isEqualTo(
            SWAGGER_SPECIFICATION
        )
    }

    @Test
    fun readTitle_shouldReturnTitle() {
        assertThat(specificationDataParser.extractTitle(SWAGGER_SPECIFICATION)).isEqualTo("Swagger Petstore")
    }

    @Test
    fun readVersion_shouldReturnVersion() {
        assertThat(specificationDataParser.extractVersion(SWAGGER_SPECIFICATION)).isEqualTo("1.0.0")
    }

    @Test
    fun readDescription_shouldReturnDescription() {
        assertThat(specificationDataParser.extractDescription(SWAGGER_SPECIFICATION)).isEqualTo("My Description")
    }

    @Test
    fun readTitle_shouldFailWhenNoTitleIsGiven() {
        assertThatThrownBy { specificationDataParser.extractTitle(SWAGGER_SPECIFICATION_WITHOUT_TITLE) }.isInstanceOf(
            SpecificationParseException::class.java
        )
    }

    @Test
    fun readVersion_shouldFailWhenNoVersionIsGiven() {
        assertThatThrownBy { specificationDataParser.extractVersion(SWAGGER_SPECIFICATION_WITHOUT_VERSION) }.isInstanceOf(
            SpecificationParseException::class.java
        )
    }

    @Test
    fun readDescription_shouldReturnNullWhenNoDescriptionIsGiven() {
        assertThat(specificationDataParser.extractDescription(SWAGGER_SPECIFICATION_WITHOUT_DESCRIPTION)).isNull()
    }

    @Test
    fun isYaml_shouldReturnTrueWhenYamlIsGiven() {
        assertThat(specificationDataParser.isYaml(YAML_SPECIFICATION)).isTrue()
    }

    @Test
    fun isYaml_shouldReturnFalseWhenYamlCantBeParsed() {
        given(yamlMapper.readTree(YAML_SPECIFICATION)).willThrow(IOException())

        assertThat(specificationDataParser.isYaml(YAML_SPECIFICATION)).isFalse()
    }

    @Test
    fun isJson_shouldRetrunTrueWhenJsonIsGiven() {
        assertThat(specificationDataParser.isJson(SWAGGER_SPECIFICATION)).isTrue()
    }

    @Test
    fun isJson_shouldReturnFalseWhenJsonCantBeParsed() {
        given(objectMapper.readTree(SWAGGER_SPECIFICATION)).willThrow(IOException())

        assertThat(specificationDataParser.isJson(SWAGGER_SPECIFICATION)).isFalse()
    }
}