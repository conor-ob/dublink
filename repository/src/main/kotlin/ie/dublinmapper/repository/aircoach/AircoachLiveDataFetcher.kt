package ie.dublinmapper.repository.aircoach

import com.nytimes.android.external.store3.base.Fetcher
import ie.dublinmapper.service.aircoach.AircoachApi
import ie.dublinmapper.service.aircoach.ServiceResponseJson
import io.reactivex.Single

class AircoachLiveDataFetcher(
    private val api: AircoachApi
) : Fetcher<ServiceResponseJson, String> {

    override fun fetch(key: String): Single<ServiceResponseJson> {
        return api.getLiveData(key)
    }

}
