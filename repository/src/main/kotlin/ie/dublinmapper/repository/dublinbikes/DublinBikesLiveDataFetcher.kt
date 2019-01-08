package ie.dublinmapper.repository.dublinbikes

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.jcdecaux.JcDecauxApi
import ie.dublinmapper.service.jcdecaux.StationJson
import io.reactivex.Single

class DublinBikesLiveDataFetcher(
    private val api: JcDecauxApi,
    private val jcDecauxApiKey: String,
    private val jcDecauxContract: String
) : Fetcher<StationJson, String> {

    override fun fetch(key: String): Single<StationJson> {
        return api.station(key, jcDecauxContract, jcDecauxApiKey)
    }

}
