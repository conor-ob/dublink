package ie.dublinmapper.core.mapping

import com.xwray.groupie.Group
import ie.dublinmapper.domain.model.LiveDataResponse
import ie.dublinmapper.util.Service
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.impl.DefaultMapperFactory
import org.junit.Before
import org.junit.Test

import com.google.common.truth.Truth.assertThat
import ie.dublinmapper.domain.model.createAircoachLiveData
import ie.dublinmapper.model.DividerItem
import ie.dublinmapper.model.HeaderItem
import ie.dublinmapper.model.aircoach.AircoachLiveDataItem
import ie.dublinmapper.util.MockStringProvider

class LiveDataDomainToUiMapperTest {

    private lateinit var mapper: MapperFacade

    @Before
    fun setup() {
        val mapperFactory = DefaultMapperFactory.Builder().useBuiltinConverters(false).build()

        mapperFactory.converterFactory.apply {
            registerConverter(LiveDataDomainToUiMapper(MockStringProvider))
        }

        mapper = mapperFactory.mapperFacade
    }

    @Test
    fun `empty Aircoach live data should map to an empty group`() {
        val liveDataResponse = LiveDataResponse(Service.AIRCOACH, "Dublin Airport Terminal 1", emptyList())
        val group = mapper.map(liveDataResponse, Group::class.java)
        assertThat(group.itemCount).isEqualTo(0)
    }

    @Test
    fun `test Aircoach happy path`() {
        val liveData = listOf(
            createAircoachLiveData(destination = "Dublin Airport Terminal 1"),
            createAircoachLiveData(destination = "Belfast"),
            createAircoachLiveData(destination = "Dublin Airport Terminal 1"),
            createAircoachLiveData(destination = "Greystones")
        )
        val liveDataResponse = LiveDataResponse(Service.AIRCOACH, "Dublin Airport Terminal 1", liveData)
        val group = mapper.map(liveDataResponse, Group::class.java)

        assertThat(group.itemCount).isEqualTo(9)
        assertThat(group.getItem(0)).isInstanceOf(DividerItem::class.java)
        assertThat(group.getItem(1)).isInstanceOf(HeaderItem::class.java)
        assertThat(group.getItem(2)).isInstanceOf(AircoachLiveDataItem::class.java)
        assertThat(group.getItem(3)).isInstanceOf(AircoachLiveDataItem::class.java)
        assertThat(group.getItem(4)).isInstanceOf(DividerItem::class.java)
        assertThat(group.getItem(5)).isInstanceOf(HeaderItem::class.java)
        assertThat(group.getItem(6)).isInstanceOf(AircoachLiveDataItem::class.java)
        assertThat(group.getItem(7)).isInstanceOf(AircoachLiveDataItem::class.java)
        assertThat(group.getItem(8)).isInstanceOf(DividerItem::class.java)
    }

}
