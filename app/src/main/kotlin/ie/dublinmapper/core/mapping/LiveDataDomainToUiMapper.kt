package ie.dublinmapper.core.mapping

import com.xwray.groupie.Group
import com.xwray.groupie.Section
import ie.dublinmapper.domain.model.*
import ie.dublinmapper.model.*
import ie.dublinmapper.model.aircoach.AircoachLiveDataItem
import ie.dublinmapper.model.buseireann.BusEireannLiveDataItem
import ie.dublinmapper.model.dart.DartLiveDataItem
import ie.dublinmapper.model.dublinbus.DublinBusLiveDataItem
import ie.dublinmapper.model.luas.LuasLiveDataItem
import ie.dublinmapper.util.Service
import ie.dublinmapper.util.StringProvider
import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.metadata.Type

class LiveDataDomainToUiMapper(
    private val stringProvider: StringProvider
) : CustomConverter<LiveDataResponse, Group>() {

    //TODO write tests eg. empty live data returns empty group, no departures returns no "Departures" header item etc
    override fun convert(
        source: LiveDataResponse,
        destinationType: Type<out Group>,
        mappingContext: MappingContext?
    ): Group {
        return when (source.service) {
            Service.AIRCOACH -> mapAircoachLiveData(source.serviceLocationName, source.liveData.map { it as AircoachLiveData })
            Service.BUS_EIREANN -> mapBusEireannLiveData(source.serviceLocationName, source.liveData.map { it as BusEireannLiveData })
            Service.DUBLIN_BIKES -> TODO()
            Service.DUBLIN_BUS -> mapDublinBusLiveData(source.serviceLocationName, source.liveData.map { it as DublinBusLiveData })
            Service.IRISH_RAIL -> mapDartLiveData(source.serviceLocationName, source.liveData.map { it as DartLiveData })
            Service.LUAS -> mapLuasLiveData(source.serviceLocationName, source.liveData.map { it as LuasLiveData })
            Service.SWORDS_EXPRESS -> TODO()
        }
    }

    private fun mapAircoachLiveData(serviceLocationName: String, liveData: List<AircoachLiveData>): Group {
        val (terminating, departures) = liveData.partition { it.destination == serviceLocationName.replace(":", ",") }
        val items = mutableListOf<Group>()
        if (departures.isNotEmpty()) {
            items.add(DividerItem())
            items.add(HeaderItem(stringProvider.departures()))
        }
        for (i in 0 until departures.size) {
            val isLast = i == departures.size - 1
            val isEven = i % 2 == 0
            items.add(AircoachLiveDataItem(departures[i], isEven, isLast))
        }
        if (terminating.isNotEmpty()) {
            items.add(DividerItem())
            items.add(HeaderItem(stringProvider.terminating()))
        }
        for (i in 0 until terminating.size) {
            val isLast = i == terminating.size - 1
            val isEven = i % 2 == 0
            items.add(AircoachLiveDataItem(terminating[i], isEven, isLast))
        }
        if (items.isNotEmpty()) {
            items.add(DividerItem())
        }
        return Section(items)
    }

    private fun mapBusEireannLiveData(serviceLocationName: String, liveData: List<BusEireannLiveData>): Group {
        val items = mutableListOf<Group>()
        if (liveData.isNotEmpty()) {
            items.add(HeaderItem(stringProvider.departures()))
        }
        for (i in 0 until liveData.size) {
            val isLast = i == liveData.size - 1
            val isEven = i % 2 == 0
            items.add(BusEireannLiveDataItem(liveData[i], isEven, isLast))
        }
        if (items.isNotEmpty()) {
            items.add(DividerItem())
        }
        return Section(items)
    }

    private fun mapDublinBusLiveData(serviceLocationName: String, liveData: List<DublinBusLiveData>): Group {
        val items = mutableListOf<Group>()
        if (liveData.isNotEmpty()) {
            items.add(HeaderItem(stringProvider.departures()))
        }
        for (i in 0 until liveData.size) {
            val isLast = i == liveData.size - 1
            val isEven = i % 2 == 0
            items.add(DublinBusLiveDataItem(liveData[i], isEven, isLast))
        }
        if (items.isNotEmpty()) {
            items.add(DividerItem())
        }
        return Section(items)
    }

    private fun mapDartLiveData(serviceLocationName: String, liveData: List<DartLiveData>): Group {
        val (terminating, departures) = liveData.partition { it.destination == serviceLocationName }
        val groups = departures.groupBy { it.direction }
        val items = mutableListOf<Group>()
        if (groups.isNotEmpty()) {
            items.add(DividerItem())
        }
        for (entry in groups.entries) {
            items.add(HeaderItem(entry.key))
            val values = entry.value
            for (i in 0 until values.size) {
                val isLast = i == values.size - 1
                val isEven = i % 2 == 0
                items.add(DartLiveDataItem(values[i], isEven, isLast))
            }
            items.add(DividerItem())
        }
        if (terminating.isNotEmpty()) {
            items.add(HeaderItem(stringProvider.terminating()))
            for (i in 0 until terminating.size) {
                val isLast = i == terminating.size - 1
                val isEven = i % 2 == 0
                items.add(DartLiveDataItem(terminating[i], isEven, isLast))
            }
            items.add(DividerItem())
        }
        return Section(items)
    }

    private fun mapLuasLiveData(serviceLocationName: String, liveData: List<LuasLiveData>): Group {
        val groups = liveData.groupBy { it.direction }
        val items = mutableListOf<Group>()
        if (liveData.isNotEmpty()) {
            items.add(DividerItem())
        }
        for (entry in groups.entries) {
            items.add(HeaderItem(entry.key))
            val values = entry.value
            for (i in 0 until values.size) {
                val isLast = i == values.size - 1
                val isEven = i % 2 == 0
                items.add(LuasLiveDataItem(values[i], isEven, isLast))
            }
            items.add(DividerItem())
        }
        return Section(items)
    }

}
