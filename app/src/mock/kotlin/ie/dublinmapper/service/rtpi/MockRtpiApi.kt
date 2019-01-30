package ie.dublinmapper.service.rtpi

import ie.dublinmapper.util.MockJsonUtils
import io.reactivex.Single
import java.lang.IllegalStateException

class MockRtpiApi : RtpiApi {

    override fun busStopInformation(operator: String, format: String): Single<RtpiBusStopInformationResponseJson> {
        return when (operator) {
            "bac" -> singleResponse("rtpi_dublin_bus_bus_stop_information", RtpiBusStopInformationResponseJson::class.java)
            "gad" -> singleResponse("rtpi_go_ahead_dublin_bus_stop_information", RtpiBusStopInformationResponseJson::class.java)
            "luas" -> singleResponse("rtpi_luas_bus_stop_information", RtpiBusStopInformationResponseJson::class.java)
            else -> throw IllegalStateException("Unknown operator: $operator")
        }
    }

    override fun routelistInformationWithVariants(format: String): Single<RtpiRouteListInformationWithVariantsResponseJson> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun routeInformation(
        routeId: String,
        operator: String,
        format: String
    ): Single<RtpiRouteInformationResponseJson> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun realTimeBusInformation(
        stopId: String,
        operator: String,
        format: String
    ): Single<RtpiRealTimeBusInformationResponseJson> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun <T> singleResponse(fileName: String, type: Class<T>): Single<T> {
        return Single.just(MockJsonUtils.deserialize(fileName, type))
    }

}
