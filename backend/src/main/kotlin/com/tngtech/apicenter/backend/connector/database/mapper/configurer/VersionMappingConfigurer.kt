package com.tngtech.apicenter.backend.connector.database.mapper.configurer

import com.tngtech.apicenter.backend.connector.database.entity.VersionEntity
import com.tngtech.apicenter.backend.connector.database.mapper.converter.VersionConverter
import com.tngtech.apicenter.backend.domain.entity.Version
import ma.glasnost.orika.MapperFactory
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer
import org.springframework.stereotype.Component

@Component
class VersionMappingConfigurer constructor(private val versionConverter: VersionConverter) :
    OrikaMapperFactoryConfigurer {

    override fun configure(orikaMapperFactory: MapperFactory) {
        orikaMapperFactory.converterFactory.registerConverter(versionConverter)

        orikaMapperFactory.classMap(VersionEntity::class.java, Version::class.java)
            .field("versionId.version", "metadata.version")
            .field("title", "metadata.title")
            .field("description", "metadata.description")
            .field("language", "metadata.language")
            .field("endpointUrl", "metadata.endpointUrl")
            .byDefault()
            .register()
    }
}