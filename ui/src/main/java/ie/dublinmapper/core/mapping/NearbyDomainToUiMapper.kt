package ie.dublinmapper.core.mapping

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.domain.model.*
import ie.dublinmapper.domain.usecase.NearbyResponse
import ie.dublinmapper.model.DividerItem
import ie.dublinmapper.model.HeaderItem
import ie.dublinmapper.model.aircoach.AircoachStopItem
import ie.dublinmapper.model.buseireann.BusEireannStopItem
import ie.dublinmapper.model.dart.DartStationItem
import ie.dublinmapper.model.dublinbikes.DublinBikesDockItem
import ie.dublinmapper.model.dublinbus.DublinBusStopItem
import ie.dublinmapper.model.luas.LuasStopItem
import ie.dublinmapper.util.StringProvider
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

class NearbyDomainToUiMapper(
    private val stringProvider: StringProvider
) : CustomConverter<NearbyResponse, Group>() {

    override fun convert(
        source: NearbyResponse,
        destinationType: Type<out Group>,
        mappingContext: MappingContext
    ): Group {
        val items = mutableListOf<Group>()
        if (source.serviceLocations.isNotEmpty()) {
            items.add(DividerItem())
            items.add(HeaderItem(stringProvider.nearby()))
        }
        for (i in 0 until source.serviceLocations.size) {
            val isLast = i == source.serviceLocations.size - 1
            val isEven = i % 2 == 0
            items.add(mapItem(source.serviceLocations[i], isEven, isLast))
        }
        if (items.isNotEmpty()) {
            items.add(DividerItem())
        }
        return Section(items)
    }

    private fun mapItem(serviceLocation: DetailedServiceLocation, isEven: Boolean, isLast: Boolean): Item {
        return when (serviceLocation) {
            is DetailedAircoachStop -> AircoachStopItem(serviceLocation, isEven, isLast)
            is DetailedBusEireannStop -> BusEireannStopItem(serviceLocation, isEven, isLast)
            is DetailedIrishRailStation -> DartStationItem(serviceLocation, isEven, isLast)
            is DetailedDublinBikesDock -> DublinBikesDockItem(serviceLocation, isEven, isLast)
            is DetailedDublinBusStop -> DublinBusStopItem(serviceLocation, isEven, isLast)
            is DetailedLuasStop -> LuasStopItem(serviceLocation, isEven, isLast)
            else -> TODO()
        }
    }

}
