package ie.dublinmapper.repository

import ie.dublinmapper.domain.repository.LiveDataKey
import ie.dublinmapper.domain.repository.LiveDataRepository
import io.reactivex.Observable
import io.rtpi.api.LiveData
import io.rtpi.api.Service

class AggregatedLiveDataRepository(
    private val liveDataRepositories: Map<Service, LiveDataRepository>
) : LiveDataRepository {

    override fun get(key: LiveDataKey): Observable<List<LiveData>> {
        return liveDataRepositories.getValue(key.service).get(key)
    }
}
