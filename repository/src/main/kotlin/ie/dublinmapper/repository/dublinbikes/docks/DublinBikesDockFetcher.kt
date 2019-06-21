package ie.dublinmapper.repository.dublinbikes.docks

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.jcdecaux.JcDecauxApi
import ie.dublinmapper.service.jcdecaux.StationJson
import ie.dublinmapper.util.Service
import io.reactivex.Single

class DublinBikesDockFetcher(
    private val api: JcDecauxApi,
    private val jcdecauxApiKey: String,
    private val jcdecauxContract: String
) : Fetcher<List<StationJson>, Service> {

    override fun fetch(key: Service): Single<List<StationJson>> {
        return api.stations(jcdecauxContract, jcdecauxApiKey)
    }

}
