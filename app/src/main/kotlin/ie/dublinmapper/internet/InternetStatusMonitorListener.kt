package ie.dublinmapper.internet

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import ie.dublinmapper.domain.internet.InternetStatusMonitor
import ie.dublinmapper.domain.internet.InternetStatusSubscriber
import timber.log.Timber

class InternetStatusMonitorListener(context: Context) : InternetStatusMonitor {

    private val subscriptions = mutableListOf<InternetStatusSubscriber>()

    init {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.registerDefaultNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {

                private var isOnline = connectivityManager.activeNetworkInfo?.isConnectedOrConnecting ?: false

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    if (!isOnline) {
                        Timber.d("[CONOR] Online")
                        isOnline = true
                        for (subscription in subscriptions) {
                            subscription.online()
                        }
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    Timber.d("[CONOR] Lost")
                    isOnline = false
                    for (subscription in subscriptions) {
                        subscription.offline()
                    }
                }
            }
        )
    }

    override fun subscribe(subscriber: InternetStatusSubscriber) {
        subscriptions.add(subscriber)
    }

    override fun unsubscribe(subscriber: InternetStatusSubscriber) {
        subscriptions.remove(subscriber)
    }
}
