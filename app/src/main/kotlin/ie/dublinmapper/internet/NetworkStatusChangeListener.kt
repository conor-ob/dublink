package ie.dublinmapper.internet

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import ie.dublinmapper.domain.internet.InternetStatus
import ie.dublinmapper.domain.internet.InternetStatusChangeListener
import ie.dublinmapper.util.getConnectivityManager
import ie.dublinmapper.util.isConnected
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class NetworkStatusChangeListener @Inject constructor(
    context: Context
) : InternetStatusChangeListener {

    private val eventEmitter = PublishSubject.create<InternetStatus>()
    private val connectivityManager = context.getConnectivityManager()?.apply {

        registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    eventEmitter.onNext(InternetStatus.ONLINE)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    eventEmitter.onNext(InternetStatus.OFFLINE)
                }
            }
        )
    }

    /**
     * Returns an observable of internet connection change events. On application startup when
     * subscribing to this observable the user doesn't need to be notified  if they are already
     * online but they should be notified if the application starts in an offline state.
     */
    override fun eventStream(): Observable<InternetStatus> = Observable.concat(
        if (connectivityManager?.isConnected() == true) {
            Observable.empty()
        } else {
            Observable.just(InternetStatus.OFFLINE)
        },
        eventEmitter
    )
}
