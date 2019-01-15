package ie.dublinmapper.service.rtpi

import ie.dublinmapper.util.MockJsonUtils
import io.reactivex.Single

class MockRtpiApi : RtpiApi {

    override fun busStopInformation(operator: String, format: String): Single<RtpiBusStopInformationResponseJson> {
        return Single.just(RtpiBusStopInformationResponseJson("", "", listOf(RtpiBusStopInformationJson(
            "123",
            "Bus Stop",
            "53.89",
            "-6.84",
            listOf(RtpiBusStopOperatorInformationJson("gad", listOf("17A")))
        ))))
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
