package ie.dublinmapper.domain.internet

interface InternetStatusMonitor {
    fun subscribe(subscriber: InternetStatusSubscriber)
    fun unsubscribe(subscriber: InternetStatusSubscriber)
}

interface InternetStatusSubscriber {
    fun onStatusChange(isOnline: Boolean)
}
