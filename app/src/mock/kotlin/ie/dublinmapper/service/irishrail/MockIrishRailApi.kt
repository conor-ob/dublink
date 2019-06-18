package ie.dublinmapper.service.irishrail

import io.reactivex.Single

class MockIrishRailApi : IrishRailApi {

    override fun getAllStationsXml(): Single<IrishRailStationResponseXml> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllStationsXmlWithStationType(stationType: String): Single<IrishRailStationResponseXml> {
        return Single.just(
            IrishRailStationResponseXml(
                stations = listOf(
                    IrishRailStationXml(
                        name = "Blackrock",
                        alias = "BROCK",
                        code = "BROCK",
                        latitude = 0.0,
                        longitude = 0.0,
                        id = "1"
                    )
                )
            )
        )
    }

    override fun getStationDataByCodeXml(stationCode: String): Single<IrishRailStationDataResponseXml> {
        return Single.just(
            IrishRailStationDataResponseXml(
                stationData = listOf(
                    IrishRailStationDataXml(
                        trainType = "DART",
                        trainCode = "DART",
                        expArrival = "10:15",
                        queryTime = "10:10:00",
                        dueIn = "5",
                        direction = "Northbound",
                        destination = "Malahide"
                    ),
                    IrishRailStationDataXml(
                        trainType = "ARROW",
                        trainCode = "D",
                        expArrival = "10:17",
                        queryTime = "10:10:00",
                        dueIn = "7",
                        direction = "Northbound",
                        destination = "Dublin Connolly"
                    ),
                    IrishRailStationDataXml(
                        trainType = "DART",
                        trainCode = "DART",
                        expArrival = "10:20",
                        queryTime = "10:10:00",
                        dueIn = "10",
                        direction = "Southbound",
                        destination = "Bray"
                    ),
                    IrishRailStationDataXml(
                        trainType = "DART",
                        trainCode = "DART",
                        expArrival = "10:31",
                        queryTime = "10:10:00",
                        dueIn = "21",
                        direction = "Southbound",
                        destination = "Greystones"
                    ),
                    IrishRailStationDataXml(
                        trainType = "DART",
                        trainCode = "DART",
                        expArrival = "11:06",
                        queryTime = "10:10:00",
                        dueIn = "56",
                        direction = "Southbound",
                        destination = "Bray"
                    )
                )
            )
        )
    }

}
