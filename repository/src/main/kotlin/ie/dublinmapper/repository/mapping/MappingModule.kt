package ie.dublinmapper.repository.mapping

import dagger.Module
import dagger.Provides
import ie.dublinmapper.repository.dart.stations.DartStationEntityToDomainMapper
import ie.dublinmapper.repository.dart.stations.DartStationJsonToEntityMapper
import ie.dublinmapper.repository.dublinbus.stops.DublinBusStopEntityToDomainMapper
import ie.dublinmapper.repository.dublinbus.stops.DublinBusStopJsonToEntityMapper
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

        converterFactory.registerConverter(DartStationJsonToEntityMapper)
        converterFactory.registerConverter(DartStationEntityToDomainMapper)

        return mapperFactory.mapperFacade
    }

}
