package ie.dublinmapper

import android.os.Bundle
import androidx.navigation.NavHost
import androidx.navigation.findNavController
import dagger.android.support.DaggerAppCompatActivity
import ie.dublinmapper.domain.model.ServiceLocation
import ie.dublinmapper.livedata.LiveDataFragmentArgs
import ie.dublinmapper.search.SearchFragmentDirections
import ie.dublinmapper.util.Service

class DublinMapperActivity : DaggerAppCompatActivity(), NavHost, Navigator {

    private val navigationController by lazy { findNavController(R.id.navHostFragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
    }

    override fun getNavController() = navigationController

    override fun onSupportNavigateUp() = navigationController.navigateUp()

    override fun navigateFavouritesToSearch() = navigationController.navigate(R.id.favouritesFragment_to_searchFragment)

    override fun navigateLiveDataToSettings() = navigationController.navigate(R.id.liveDataFragment_to_settingsFragment)

    override fun navigateSearchToLiveData(serviceLocation: ServiceLocation) {
        val styleId = getStyleId(serviceLocation.service)
        val intent = SearchFragmentDirections.searchFragmentToLivedataFragment(
            serviceLocation.id,
            serviceLocation.name,
            serviceLocation.service,
            serviceLocation.isFavourite(),
            styleId
        )
        navigationController.navigate(intent)
    }

    // TODO
    private fun getStyleId(service: Service): Int {
        return when (service) {
            Service.AIRCOACH -> R.style.AircoachTheme
            Service.BUS_EIREANN -> R.style.BusEireannTheme
            Service.DUBLIN_BIKES -> R.style.DublinBikesTheme
            Service.DUBLIN_BUS -> R.style.DublinBusTheme
            Service.IRISH_RAIL -> R.style.IrishRailTheme
            Service.LUAS -> R.style.LuasTheme
            Service.SWORDS_EXPRESS -> TODO()
        }
    }
}
