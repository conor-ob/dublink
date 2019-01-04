package ie.dublinmapper.repository.dublinbikes

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.jcdecaux.JcdecauxApi
import ie.dublinmapper.service.jcdecaux.StationJson
import io.reactivex.Single

class DublinBikesDockFetcher(
    private val api: JcdecauxApi
) : Fetcher<List<StationJson>, String> {

    override fun fetch(key: String): Single<List<StationJson>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
