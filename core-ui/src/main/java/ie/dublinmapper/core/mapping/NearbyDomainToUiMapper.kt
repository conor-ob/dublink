package ie.dublinmapper.core.mapping

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.domain.usecase.NearbyResponse
import ie.dublinmapper.model.aircoach.AircoachStopItem
import ie.dublinmapper.model.buseireann.BusEireannStopItem
import ie.dublinmapper.model.irishrail.IrishRailStationItem
import ie.dublinmapper.model.dublinbikes.DublinBikesDockItem
import ie.dublinmapper.model.dublinbus.DublinBusStopItem
import ie.dublinmapper.model.luas.LuasStopItem
import ie.dublinmapper.core.StringProvider
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
        for (entry in source.serviceLocations) {
            items.add(mapItem(entry.key, entry.value))
        }
        return Section(items)
    }

    private fun mapItem(distance: Double, serviceLocation: ServiceLocation): Item {
        return when (serviceLocation) {
            is AircoachStop -> AircoachStopItem(serviceLocation, distance)
            is BusEireannStop -> BusEireannStopItem(serviceLocation, distance)
            is IrishRailStation -> IrishRailStationItem(serviceLocation, distance)
            is DublinBikesDock -> DublinBikesDockItem(serviceLocation, distance)
            is DublinBusStop -> DublinBusStopItem(serviceLocation, distance)
            is LuasStop -> LuasStopItem(serviceLocation, distance)
            else -> TODO()
        }
    }

}
