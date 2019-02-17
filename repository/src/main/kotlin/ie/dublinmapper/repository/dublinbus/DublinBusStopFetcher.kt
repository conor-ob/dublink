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
import io.reactivex.functions.BiFunction

class DublinBusStopFetcher(
    private val dublinBusApi: DublinBusApi,
    private val rtpiApi: RtpiApi,
    private val dublinBusOperator: String,
    private val goAheadOperator: String,
    private val format: String,
    private val thread: Thread
) : Fetcher<List<RtpiBusStopInformationJson>, String> {

    override fun fetch(key: String): Single<List<RtpiBusStopInformationJson>> {
        return Single.fromObservable(fetchInternal())
    }

    private fun fetchInternal(): Observable<List<RtpiBusStopInformationJson>> {
        val requestRoot = DublinBusDestinationRequestRootXml()
        val requestBody = DublinBusDestinationRequestBodyXml(requestRoot)
        val request = DublinBusDestinationRequestXml(requestBody)
        return Observable.combineLatest(
//            dublinBusApi.getAllDestinations(request).subscribeOn(thread.io).map { it.stops }.toObservable(),
            rtpiApi.busStopInformation(dublinBusOperator, format).subscribeOn(thread.io).map { it.results }.toObservable(),
            rtpiApi.busStopInformation(goAheadOperator, format).subscribeOn(thread.io).map { it.results }.toObservable(),
            BiFunction { dublinBusStops,  goAheadDublinStops -> aggregate(dublinBusStops, goAheadDublinStops) }
        )
    }

    private fun aggregate(
//        defaultStops: List<DublinBusDestinationXml>,
        dublinBusStops: List<RtpiBusStopInformationJson>,
        goAheadDublinStops: List<RtpiBusStopInformationJson>
    ): List<RtpiBusStopInformationJson> {
        val aggregatedStops = mutableMapOf<String, RtpiBusStopInformationJson>()
//        for (stop in defaultStops) {
//            aggregatedStops[stop.id!!] = AggregatedStop(
//                stop.id!!,
//                stop.name!!,
//                Coordinate(stop.latitude!!.toDouble(), stop.longitude!!.toDouble()),
//                mapOf(Pair(Operator.DUBLIN_BUS.shortName, emptySet()))
//            )
//        }
        for (stop in dublinBusStops) {
            var aggregatedStop = aggregatedStops[stop.stopId]
            if (aggregatedStop == null) {
                aggregatedStops[stop.stopId] = stop
            } else {
                val existingOperators = aggregatedStop.operators.toMutableList()
                existingOperators.addAll(stop.operators)
                aggregatedStop = aggregatedStop.copy(operators = existingOperators)
                aggregatedStops[stop.stopId] = aggregatedStop
            }
        }
        for (stop in goAheadDublinStops) {
            var aggregatedStop = aggregatedStops[stop.stopId]
            if (aggregatedStop == null) {
                aggregatedStops[stop.stopId] = stop
            } else {
                val existingOperators = aggregatedStop.operators.toMutableList()
                existingOperators.addAll(stop.operators)
                aggregatedStop = aggregatedStop.copy(operators = existingOperators)
                aggregatedStops[stop.stopId] = aggregatedStop
            }
        }
        return aggregatedStops.values.toList()
    }

}
