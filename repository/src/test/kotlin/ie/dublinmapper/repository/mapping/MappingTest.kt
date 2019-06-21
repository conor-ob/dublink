package ie.dublinmapper.repository.mapping

import ie.dublinmapper.datamodel.dublinbus.DublinBusStopEntity
import ie.dublinmapper.repository.dublinbus.stops.DublinBusStopJsonToEntityMapper
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.service.rtpi.RtpiBusStopOperatorInformationJson
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.converter.builtin.BuiltinConverters
import ma.glasnost.orika.impl.DefaultMapperFactory
import org.junit.Before
import org.junit.Test

class MappingTest {

    private lateinit var mapperFacade: MapperFacade

    @Before
    fun setup() {
        val mapperFactory = DefaultMapperFactory.Builder().useBuiltinConverters(false).build()
        mapperFactory.converterFactory.registerConverter(DublinBusStopJsonToEntityMapper)
        BuiltinConverters.register(mapperFactory.converterFactory)
        mapperFacade = mapperFactory.mapperFacade
    }

    @Test
    fun testMap() {
        val operators = listOf(
            RtpiBusStopOperatorInformationJson(
                name = "bac",
                routes = listOf("46A", "39A", "145")
            ),
            RtpiBusStopOperatorInformationJson(
                name = "gad",
                routes = listOf("17", "75")
            )
        )
        val json = listOf(
            RtpiBusStopInformationJson(
                stopId = "123",
                fullName = "test",
                latitude = "0.0",
                longitude = "0.0",
                operators = operators
            )
        )
        val result = mapperFacade.mapAsList(json, DublinBusStopEntity::class.java)
        print(result)
    }

}
