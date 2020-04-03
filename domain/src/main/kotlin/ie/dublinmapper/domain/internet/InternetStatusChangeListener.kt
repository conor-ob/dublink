package ie.dublinmapper.domain.internet

import io.reactivex.Observable

interface InternetStatusChangeListener {
    fun eventStream(): Observable<InternetStatus>
}

enum class InternetStatus {
    ONLINE,
    OFFLINE;
}
