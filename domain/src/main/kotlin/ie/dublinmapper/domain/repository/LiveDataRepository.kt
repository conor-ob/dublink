package ie.dublinmapper.domain.repository

import io.reactivex.Observable
import io.rtpi.api.LiveData
import io.rtpi.api.Service

interface LiveDataRepository {
    fun get(key: LiveDataKey): Observable<List<LiveData>>
}

data class LiveDataKey(
    val service: Service,
    val locationId: String
)
