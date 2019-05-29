package ie.dublinmapper.repository.dublinbus.stops

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.dublinbus.*
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiBusStopInformationJson
import ie.dublinmapper.util.RxScheduler
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function3

class DublinBusStopFetcher(
    private val dublinBusApi: DublinBusApi,
    private val rtpiApi: RtpiApi,
    private val dublinBusOperator: String,
    private val goAheadOperator: String,
    private val format: String,
    private val scheduler: RxScheduler
) : Fetcher<List<RtpiBusStopInformationJson>, String> {

    override fun fetch(key: String): Single<List<RtpiBusStopInformationJson>> {
        return Single.fromObservable(fetchInternal())
    }

    private fun fetchInternal(): Observable<List<RtpiBusStopInformationJson>> {
        val requestRoot = DublinBusDestinationRequestRootXml()
        val requestBody = DublinBusDestinationRequestBodyXml(requestRoot)
        val request = DublinBusDestinationRequestXml(requestBody)
        return Observable.combineLatest(
            dublinBusApi.getAllDestinations(request).subscribeOn(scheduler.io).map { it.stops }.toObservable(),
            rtpiApi.busStopInformation(dublinBusOperator, format).subscribeOn(scheduler.io).map { it.results }.toObservable(),
            rtpiApi.busStopInformation(goAheadOperator, format).subscribeOn(scheduler.io).map { it.results }.toObservable(),
            Function3 { defaultStops, dublinBusStops, goAheadDublinStops -> aggregate(defaultStops, dublinBusStops, goAheadDublinStops) }
        )
    }

    private fun aggregate(
        defaultStops: List<DublinBusDestinationXml>,
        dublinBusStops: List<RtpiBusStopInformationJson>,
        goAheadDublinStops: List<RtpiBusStopInformationJson>
    ): List<RtpiBusStopInformationJson> {
        val aggregatedStops = mutableMapOf<String, RtpiBusStopInformationJson>()
        for (stop in defaultStops) {
            var aggregatedStop = aggregatedStops[stop.id]
            if (aggregatedStop == null) {
                aggregatedStops[stop.id!!] = RtpiBusStopInformationJson(
                    stop.id!!,
                    stop.name!!,
                    stop.latitude!!,
                    stop.longitude!!
                )
            } else {
                aggregatedStop = aggregatedStop.copy(
                    stopId = stop.id!!,
                    fullName = stop.name!!,
                    latitude = stop.latitude!!,
                    longitude = stop.longitude!!
                )
                aggregatedStops[stop.id!!] = aggregatedStop
            }
        }
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
