package ie.dublinmapper.news

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.rtpi.api.Service

class TwitterFeedAdapter(
    private val enabledServices: List<String>,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount() = enabledServices.size

    override fun getItem(position: Int): Fragment {
        return twitterFragments[enabledServices[position]] as Fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return enabledServices[position]
    }

    companion object {

        private val twitterFragments = mapOf(
            "TFI" to TwitterFragment("transport-for-ireland"),
            Service.DUBLIN_BUS.fullName to TwitterFragment("dublin-bus"),
            Service.IRISH_RAIL.fullName to TwitterFragment("irish-rail"),
            Service.LUAS.fullName to TwitterFragment("luas"),
            Service.DUBLIN_BIKES.fullName to TwitterFragment("dublin-bikes"),
            Service.BUS_EIREANN.fullName to TwitterFragment("bus-eireann"),
            Service.AIRCOACH.fullName to TwitterFragment("aircoach")
        )
    }

}
