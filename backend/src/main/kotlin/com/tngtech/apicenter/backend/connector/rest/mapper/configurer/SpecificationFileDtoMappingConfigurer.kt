package com.tngtech.apicenter.backend.connector.rest.mapper.configurer

import com.tngtech.apicenter.backend.connector.rest.dto.SpecificationDto
import com.tngtech.apicenter.backend.connector.rest.mapper.converter.SpecificationFileDtoConverter
import com.tngtech.apicenter.backend.domain.entity.Specification
import ma.glasnost.orika.MapperFactory
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer
import org.springframework.stereotype.Component

@Component
class SpecificationFileDtoMappingConfigurer constructor(private val specificationFileDtoConverter: SpecificationFileDtoConverter) :
    OrikaMapperFactoryConfigurer {

    override fun configure(orikaMapperFactory: MapperFactory) {
        orikaMapperFactory.converterFactory.registerConverter(specificationFileDtoConverter)

        orikaMapperFactory.classMap(Specification::class.java, SpecificationDto::class.java)
            .byDefault()
            .register()

    }
}