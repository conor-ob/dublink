package ie.dublinmapper.news

import android.os.Bundle
import android.view.View
import ie.dublinmapper.DublinMapperFragment
import ie.dublinmapper.DublinMapperNavigator
import io.rtpi.api.Service
import kotlinx.android.synthetic.main.fragment_news.tabs
import kotlinx.android.synthetic.main.fragment_news.toolbar
import kotlinx.android.synthetic.main.fragment_news.viewpager

class NewsFragment : DublinMapperFragment(R.layout.fragment_news) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    (activity as DublinMapperNavigator).navigateToSettings()
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
        if (enabledServiceManager.isServiceEnabled(Service.AIRCOACH)) {
            services.add(Service.AIRCOACH.fullName)
        }
        return services
    }
}
