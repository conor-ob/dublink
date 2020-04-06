package ie.dublinmapper.repository

import ie.dublinmapper.domain.repository.AggregatedLiveDataRepository
import ie.dublinmapper.domain.repository.LiveDataKey
import ie.dublinmapper.domain.repository.LiveDataRepository
import ie.dublinmapper.domain.repository.LiveDataResponse
import io.reactivex.Observable
import io.rtpi.api.Service

class DelegatingLiveDataRepository(
    private val liveDataRepositories: Map<Service, LiveDataRepository>
) : AggregatedLiveDataRepository {

    override fun get(key: LiveDataKey): Observable<LiveDataResponse> {
        return liveDataRepositories.getValue(key.service).get(key)
    }
}
