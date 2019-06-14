package ie.dublinmapper.service.irishrail

import io.reactivex.Single

class MockIrishRailApi : IrishRailApi {

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
                        expArrival = "10:10",
                        dueIn = "13",
                        direction = "Northbound",
                        destination = "Malahide"
                    ),
                    IrishRailStationDataXml(
                        trainType = "ARROW",
                        trainCode = "D",
                        expArrival = "10:15",
                        dueIn = "23",
                        direction = "Northbound",
                        destination = "Dublin Connolly"
                    ),
                    IrishRailStationDataXml(
                        trainType = "DART",
                        trainCode = "DART",
                        expArrival = "10:34",
                        dueIn = "13",
                        direction = "Southbound",
                        destination = "Bray"
                    ),
                    IrishRailStationDataXml(
                        trainType = "DART",
                        trainCode = "DART",
                        expArrival = "10:11",
                        dueIn = "13",
                        direction = "Southbound",
                        destination = "Greystones"
                    ),
                    IrishRailStationDataXml(
                        trainType = "DART",
                        trainCode = "DART",
                        expArrival = "10:01",
                        dueIn = "13",
                        direction = "Southbound",
                        destination = "Bray"
                    )
                )
            )
        )
    }

}
