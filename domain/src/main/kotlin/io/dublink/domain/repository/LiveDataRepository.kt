package io.dublink.domain.repository

import io.reactivex.Observable
import io.rtpi.api.LiveData
import io.rtpi.api.Service

interface LiveDataRepository {
    fun get(key: LiveDataKey, refresh: Boolean): Observable<LiveDataResponse>
}

data class LiveDataKey(
    val service: Service,
    val locationId: String
)

sealed class LiveDataResponse {

    data class Data(val liveData: List<LiveData>) : LiveDataResponse()

    data class Error(val throwable: Throwable) : LiveDataResponse()
}
