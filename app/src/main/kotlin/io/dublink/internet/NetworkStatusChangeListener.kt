package io.dublink.internet

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import io.dublink.domain.internet.InternetStatus
import io.dublink.domain.internet.InternetStatusChangeListener
import io.dublink.util.getConnectivityManager
import io.dublink.util.isConnected
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class NetworkStatusChangeListener @Inject constructor(
    context: Context
) : InternetStatusChangeListener {

    private val eventEmitter = PublishSubject.create<InternetStatus>()
    private val connectivityManager = context.getConnectivityManager()?.apply {

        registerDefaultNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {

                var isOnline = isConnected()

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    if (!isOnline) {
                        isOnline = true
                        eventEmitter.onNext(InternetStatus.ONLINE)
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    if (isOnline) {
                        isOnline = false
                        eventEmitter.onNext(InternetStatus.OFFLINE)
                    }
                }
            }
        )
    }

    /**
     * Returns an observable of internet connection change events. On application startup when
     * subscribing to this observable the user doesn't need to be notified if they are already
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
