package ie.dublinmapper.ui.mapping

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import ie.dublinmapper.domain.usecase.FavouritesResponse
import ie.dublinmapper.model.aircoach.AircoachStopItem
import ie.dublinmapper.model.buseireann.BusEireannStopItem
import ie.dublinmapper.model.irishrail.IrishRailStationItem
import ie.dublinmapper.model.dublinbikes.DublinBikesDockItem
import ie.dublinmapper.model.dublinbus.DublinBusStopItem
import ie.dublinmapper.model.luas.LuasStopItem
import ie.dublinmapper.domain.service.StringProvider
import io.rtpi.api.*
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

class FavouritesDomainToUiMapper(
    private val stringProvider: StringProvider
) : CustomConverter<FavouritesResponse, Group>() {

    override fun convert(
        source: FavouritesResponse,
        destinationType: Type<out Group>,
        mappingContext: MappingContext
    ): Group {
        val items = mutableListOf<Group>()
//        if (source.serviceLocations.isNotEmpty()) {
//            items.add(DividerItem())
//            items.add(HeaderItem(stringProvider.favourites()))
//        }
        for (i in 0 until source.serviceLocations.size) {
            items.add(mapItem(source.serviceLocations[i]))
        }
//        if (items.isNotEmpty()) {
//            items.add(DividerItem())
//        }
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
