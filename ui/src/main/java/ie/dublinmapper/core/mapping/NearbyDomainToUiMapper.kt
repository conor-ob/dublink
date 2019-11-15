package ie.dublinmapper.core.mapping

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.domain.usecase.NearbyResponse
import ie.dublinmapper.model.DividerItem
import ie.dublinmapper.model.HeaderItem
import ie.dublinmapper.model.aircoach.AircoachStopItem
import ie.dublinmapper.model.buseireann.BusEireannStopItem
import ie.dublinmapper.model.dart.IrishRailStationItem
import ie.dublinmapper.model.dublinbikes.DublinBikesDockItem
import ie.dublinmapper.model.dublinbus.DublinBusStopItem
import ie.dublinmapper.model.luas.LuasStopItem
import ie.dublinmapper.util.StringProvider
import io.rtpi.api.*
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
            items.add(mapItem(source.serviceLocations[i]))
        }
        if (items.isNotEmpty()) {
            items.add(DividerItem())
        }
        return Section(items)
    }

    private fun mapItem(serviceLocation: ServiceLocation): Item {
        return when (serviceLocation) {
            is AircoachStop -> AircoachStopItem(serviceLocation)
            is BusEireannStop -> BusEireannStopItem(serviceLocation)
            is IrishRailStation -> IrishRailStationItem(serviceLocation)
            is DublinBikesDock -> DublinBikesDockItem(serviceLocation)
            is DublinBusStop -> DublinBusStopItem(serviceLocation)
            is LuasStop -> LuasStopItem(serviceLocation)
            else -> TODO()
        }
    }

}
