package ie.dublinmapper.repository.dublinbus

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.dublinbus.*
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.util.Coordinate
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.Thread
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function3

class DublinBusStopFetcher(
    private val dublinBusApi: DublinBusApi,
    private val rtpiApi: RtpiApi,
    private val dublinBusOperator: String,
    private val goAheadOperator: String,
    private val format: String,
    private val thread: Thread
) : Fetcher<List<AggregatedStop>, String> {

    override fun fetch(key: String): Single<List<AggregatedStop>> {
        return Single.fromObservable(fetchInternal())
    }

    private fun fetchInternal(): Observable<List<AggregatedStop>> {
        val requestRoot = DublinBusDestinationRequestRootXml()
        val requestBody = DublinBusDestinationRequestBodyXml(requestRoot)
        val request = DublinBusDestinationRequestXml(requestBody)
        return Observable.combineLatest(
            dublinBusApi.getAllDestinations(request).subscribeOn(thread.io).map { it.stops }.toObservable(),
            rtpiApi.busStopInformation(dublinBusOperator, format).subscribeOn(thread.io).map { it.results }.toObservable(),
            rtpiApi.busStopInformation(goAheadOperator, format).subscribeOn(thread.io).map { it.results }.toObservable(),
            Function3 { defaultStops, dublinBusStops, goAheadDublinStops ->
                aggregate(defaultStops, dublinBusStops, goAheadDublinStops)
            }
        )
    }

    private fun aggregate(
        defaultStops: List<DublinBusDestinationXml>,
        dublinBusStops: List<RtpiBusStopInformationJson>,
        goAheadDublinStops: List<RtpiBusStopInformationJson>
    ): List<AggregatedStop> {
        val aggregatedStops = mutableMapOf<String, AggregatedStop>()
        for (stop in defaultStops) {
            aggregatedStops[stop.id!!] = AggregatedStop(
                stop.id!!,
                stop.name!!,
                Coordinate(stop.latitude!!.toDouble(), stop.longitude!!.toDouble()),
                mapOf(Pair(Operator.DUBLIN_BUS.shortName, emptySet()))
            )
        }
        for (stop in dublinBusStops) {
            val routes = mutableSetOf<String>()
            stop.operators.forEach { routes.addAll(it.routes) }
            val aggregatedStop = aggregatedStops[stop.stopId]
            if (aggregatedStop == null) {
                aggregatedStops[stop.stopId] = AggregatedStop(
                    stop.stopId,
                    stop.fullName,
                    Coordinate(stop.latitude.toDouble(), stop.longitude.toDouble()),
                    mapOf(Pair(Operator.DUBLIN_BUS.shortName, routes))
                )
            } else {
                val newRoutes = aggregatedStop.operatorsByRoute.toMutableMap()
                newRoutes[Operator.DUBLIN_BUS.shortName] = routes
                aggregatedStops[stop.stopId] = aggregatedStop.copy(
                    operatorsByRoute = newRoutes
                )
            }
        }
        for (stop in goAheadDublinStops) {
            val routes = mutableSetOf<String>()
            stop.operators.forEach { routes.addAll(it.routes) }
            val aggregatedStop = aggregatedStops[stop.stopId]
            if (aggregatedStop == null) {
                aggregatedStops[stop.stopId] = AggregatedStop(
                    stop.stopId,
                    stop.fullName,
                    Coordinate(stop.latitude.toDouble(), stop.longitude.toDouble()),
                    mapOf(Pair(Operator.GO_AHEAD.shortName, routes))
                )
            } else {
                val newRoutes = aggregatedStop.operatorsByRoute.toMutableMap()
                newRoutes[Operator.GO_AHEAD.shortName] = routes
                aggregatedStops[stop.stopId] = aggregatedStop.copy(
                    operatorsByRoute = newRoutes
                )
            }
        }
        return aggregatedStops.values.toList()
    }

}

data class AggregatedStop(
    val id: String,
    val name: String,
    val coordinate: Coordinate,
    val operatorsByRoute: Map<String, Set<String>>
)
