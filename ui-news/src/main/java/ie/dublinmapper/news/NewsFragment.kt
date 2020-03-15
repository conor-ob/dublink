package ie.dublinmapper.news

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import ie.dublinmapper.Navigator
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
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    (activity as Navigator).navigateToSettings()
                    return@setOnMenuItemClickListener true
                }
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(menuItem)
        }
        viewpager.adapter = TwitterFeedAdapter(
            getSortedServices(),
            childFragmentManager
        )
        tabs.setupWithViewPager(viewpager)
    }

    private fun getSortedServices(): List<String> {
        val services = mutableListOf<String>()
        services.add("TFI")
        if (enabledServiceManager.isServiceEnabled(Service.DUBLIN_BUS)) {
            services.add(Service.DUBLIN_BUS.fullName)
        }
        if (enabledServiceManager.isServiceEnabled(Service.IRISH_RAIL)) {
            services.add(Service.IRISH_RAIL.fullName)
        }
        if (enabledServiceManager.isServiceEnabled(Service.LUAS)) {
            services.add(Service.LUAS.fullName)
        }
        if (enabledServiceManager.isServiceEnabled(Service.BUS_EIREANN)) {
            services.add(Service.BUS_EIREANN.fullName)
        }
        if (enabledServiceManager.isServiceEnabled(Service.DUBLIN_BIKES)) {
            services.add(Service.DUBLIN_BIKES.fullName)
        }
        if (enabledServiceManager.isServiceEnabled(Service.AIRCOACH)) {
            services.add(Service.AIRCOACH.fullName)
        }
        return services
    }

}
