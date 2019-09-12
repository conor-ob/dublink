package ie.dublinmapper.repository.dublinbus.livedata

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.dublinbus.*
import ie.dublinmapper.service.rtpi.RtpiApi
import ie.dublinmapper.service.rtpi.RtpiRealTimeBusInformationJson
import ie.dublinmapper.util.Formatter
import ie.dublinmapper.util.Operator
import ie.dublinmapper.util.TimeUtils
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.temporal.ChronoUnit

class DublinBusLiveDataFetcher(
    private val dublinBusApi: DublinBusApi,
    private val rtpiApi: RtpiApi,
    private val dublinBusOperator: String,
    private val goAheadOperator: String,
    private val format: String
) : Fetcher<List<RtpiRealTimeBusInformationJson>, String> {

    //TODO merge API responses
    override fun fetch(stopId: String): Single<List<RtpiRealTimeBusInformationJson>> {
        return Single.zip(
            fetchDublinBusLiveData(stopId).subscribeOn(Schedulers.newThread()),
            fetchRtpiLiveData(stopId).subscribeOn(Schedulers.newThread()),
            BiFunction { dublinBusLiveData, rtpiLiveData ->
                aggregate(dublinBusLiveData, rtpiLiveData)
            }
        )
    }

    private fun aggregate(
        dublinBusLiveData: List<DublinBusRealTimeStopDataXml>,
        rtpiLiveData: List<RtpiRealTimeBusInformationJson>
    ): List<RtpiRealTimeBusInformationJson> {
        val aggregated = mutableListOf<RtpiRealTimeBusInformationJson>()
        for (liveData in dublinBusLiveData) {
            aggregated.add(
                RtpiRealTimeBusInformationJson(
                    route = liveData.routeId,
                    operator = Operator.DUBLIN_BUS.shortName,
                    destination = liveData.destination,
                    arrivalDateTime = liveData.expectedTimestamp,
                    dueTime = minutesBetweenTimestamps(liveData.expectedTimestamp!!, liveData.responseTimestamp!!).toString()
                )
            )
        }
        for (liveData in rtpiLiveData) {
            aggregated.add(
                RtpiRealTimeBusInformationJson(
                    route = liveData.route,
                    operator = Operator.GO_AHEAD.shortName,
                    destination = liveData.destination,
                    arrivalDateTime = liveData.arrivalDateTime,
                    dueTime = liveData.dueTime
                )
            )
        }
        return aggregated
    }

    private fun fetchDublinBusLiveData(stopId: String): Single<List<DublinBusRealTimeStopDataXml>> {
        val requestRoot = DublinBusRealTimeStopDataRequestRootXml(stopId, true.toString())
        val requestBody = DublinBusRealTimeStopDataRequestBodyXml(requestRoot)
        val request = DublinBusRealTimeStopDataRequestXml(requestBody)
        return dublinBusApi.getRealTimeStopData(request)
            .map { response ->
                response.dublinBusRealTimeStopData
                    .filter {
                        it.routeId != null
                                && it.destination != null
                                && it.expectedTimestamp != null
                    }
                    .map {
                        it.copy(
                            routeId = it.routeId!!.trim(),
                            destination = it.destination!!.trim(),
                            expectedTimestamp = it.expectedTimestamp!!.trim()
                        )
                    }
            }
    }

    private fun fetchRtpiLiveData(stopId: String): Single<List<RtpiRealTimeBusInformationJson>> {
        return rtpiApi.realTimeBusInformation(stopId, goAheadOperator, format)
            .map { response ->
                response.results
                    .filter {
                        it.route != null
                                && it.destination != null
                                && it.operator != null
                                && it.arrivalDateTime != null
                    }
                    .map {
                        it.copy(
                            route = it.route!!.trim(),
                            operator = it.operator!!.trim().toUpperCase(),
                            destination = it.destination!!.trim(),
                            arrivalDateTime = toIso8601Timestamp(it.arrivalDateTime)
                        )
                    }
            }
    }

    private fun toIso8601Timestamp(timestamp: String?): String? {
        if (timestamp == null) {
            return null
        }
        return TimeUtils.toIso8601Timestamp(timestamp, Formatter.dateTime)
    }

    private fun parseDueTime(dueTime: String): Long {
        if ("Due".equals(dueTime, ignoreCase = true)) {
            return 0L
        }
        try {
            return dueTime.toLong()
        } catch (e: NumberFormatException) {
            // safety
        }
        return 0L
    }

    private fun minutesBetweenTimestamps(expectedTimestamp: String, responseTimestamp: String): Long {
        val responseInstant = TimeUtils.dateTimeStampToInstant(responseTimestamp, Formatter.isoDateTime)
        val expectedInstant = TimeUtils.dateTimeStampToInstant(expectedTimestamp, Formatter.isoDateTime)
        return TimeUtils.timeBetween(ChronoUnit.MINUTES, responseInstant, expectedInstant)
    }

}
