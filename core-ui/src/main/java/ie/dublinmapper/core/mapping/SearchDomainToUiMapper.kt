package ie.dublinmapper.core.mapping

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.domain.usecase.SearchResponse
import ie.dublinmapper.model.HeaderItem
import ie.dublinmapper.model.aircoach.AircoachStopItem
import ie.dublinmapper.model.buseireann.BusEireannStopItem
import ie.dublinmapper.model.dart.IrishRailStationItem
import ie.dublinmapper.model.dublinbikes.DublinBikesDockItem
import ie.dublinmapper.model.dublinbus.DublinBusStopItem
import ie.dublinmapper.model.luas.LuasStopItem
import io.rtpi.api.*
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

object SearchDomainToUiMapper : CustomConverter<SearchResponse, Group>() {

    override fun convert(
        source: SearchResponse,
        destinationType: Type<out Group>,
        mappingContext: MappingContext
    ): Group {
        val items = mutableListOf<Group>()
        val serviceLocations = source.serviceLocations.groupBy { it.service }
        for (entry in serviceLocations) {
//            items.add(HeaderItem(entry.key.fullName))
            items.addAll(entry.value.map { mapItem(it) })
        }
        return Section(items)
    }

    private fun mapItem(serviceLocation: ServiceLocation): Item {
        return when (serviceLocation) {
            is AircoachStop -> AircoachStopItem(serviceLocation, null)
            is BusEireannStop -> BusEireannStopItem(serviceLocation, null)
            is IrishRailStation -> IrishRailStationItem(serviceLocation, null)
            is DublinBikesDock -> DublinBikesDockItem(serviceLocation, null)
            is DublinBusStop -> DublinBusStopItem(serviceLocation, null)
            is LuasStop -> LuasStopItem(serviceLocation, null)
            else -> TODO()
        }
    }

}
