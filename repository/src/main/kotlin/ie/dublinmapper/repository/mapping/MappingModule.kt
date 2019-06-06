package ie.dublinmapper.repository.mapping

import dagger.Module
import dagger.Provides
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.impl.DefaultMapperFactory
import javax.inject.Singleton

@Module
class MappingModule {

    @Provides
    @Singleton
    fun mapperFacade(): MapperFacade {
        val mapperFactory = DefaultMapperFactory.Builder().useBuiltinConverters(false).build()
        val converterFactory = mapperFactory.converterFactory
        converterFactory.registerConverter(DublinBusStopJsonToEntityMapper)
        converterFactory.registerConverter(DublinBusStopEntityToDomainMapper)
        return mapperFactory.mapperFacade
    }

}
