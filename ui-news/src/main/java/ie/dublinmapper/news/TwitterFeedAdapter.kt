package ie.dublinmapper.news

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.rtpi.api.Service

class TwitterFeedAdapter(
    private val enabledServices: List<Service>,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount() = enabledServices.size

    override fun getItem(position: Int): Fragment {
        return twitterFragments[enabledServices[position]] as Fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return enabledServices[position].fullName
    }

    companion object {

        private val twitterFragments = mapOf(
            Service.DUBLIN_BUS to TwitterFragment("dublin-bus"),
            Service.IRISH_RAIL to TwitterFragment("irish-rail"),
            Service.LUAS to TwitterFragment("luas"),
            Service.DUBLIN_BIKES to TwitterFragment("dublin-bikes"),
            Service.BUS_EIREANN to TwitterFragment("bus-eireann"),
            Service.AIRCOACH to TwitterFragment("aircoach")
        )
    }

}
