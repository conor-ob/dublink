package ie.dublinmapper.news

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import ie.dublinmapper.ui.DublinMapperFragment
import io.rtpi.api.Service
import kotlinx.android.synthetic.main.fragment_news.*

class NewsFragment : DublinMapperFragment(R.layout.fragment_news) {

    companion object {

        private var twitterIsInitialized = false

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!twitterIsInitialized) {
            val appInfo = requireContext().applicationContext.packageManager.getApplicationInfo(
                requireContext().applicationContext.packageName, PackageManager.GET_META_DATA
            )
            val config = TwitterConfig.Builder(requireContext().applicationContext)
                .twitterAuthConfig(
                    TwitterAuthConfig(
                        appInfo.metaData.getString("com.twitter.sdk.android.CONSUMER_KEY"),
                        appInfo.metaData.getString("com.twitter.sdk.android.CONSUMER_SECRET")
                    )
                ).build()
            Twitter.initialize(config)
            twitterIsInitialized = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewpager.adapter = TwitterFeedAdapter(
            getSortedServices(),
            childFragmentManager
        )
        tabs.setupWithViewPager(viewpager)
    }

    private fun getSortedServices(): List<Service> {
        val services = mutableListOf<Service>()
        if (enabledServiceManager.isServiceEnabled(Service.DUBLIN_BUS)) {
            services.add(Service.DUBLIN_BUS)
        }
        if (enabledServiceManager.isServiceEnabled(Service.IRISH_RAIL)) {
            services.add(Service.IRISH_RAIL)
        }
        if (enabledServiceManager.isServiceEnabled(Service.LUAS)) {
            services.add(Service.LUAS)
        }
        if (enabledServiceManager.isServiceEnabled(Service.BUS_EIREANN)) {
            services.add(Service.BUS_EIREANN)
        }
        if (enabledServiceManager.isServiceEnabled(Service.DUBLIN_BIKES)) {
            services.add(Service.DUBLIN_BIKES)
        }
        if (enabledServiceManager.isServiceEnabled(Service.AIRCOACH)) {
            services.add(Service.AIRCOACH)
        }
        return services
    }

}
