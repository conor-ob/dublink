package ie.dublinmapper.service.aircoach

import io.reactivex.Single

interface AircoachScraper {

    fun getStops(): Single<List<AircoachStopJson>>

}
