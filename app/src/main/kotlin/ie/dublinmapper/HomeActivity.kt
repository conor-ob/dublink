package ie.dublinmapper

import android.os.Bundle
import androidx.navigation.NavHost
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import dagger.android.support.DaggerAppCompatActivity
import ie.dublinmapper.domain.model.isFavourite
import ie.dublinmapper.favourites.FavouritesFragmentDirections
import ie.dublinmapper.nearby.NearbyFragmentDirections
import ie.dublinmapper.search.SearchFragmentDirections
import io.rtpi.api.ServiceLocation
import kotlinx.android.synthetic.main.activity_root.*

class HomeActivity : DaggerAppCompatActivity(), NavHost, Navigator {

    private val navigationController by lazy { findNavController(R.id.navHostFragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        navigation.setupWithNavController(navigationController)
        navigation.setOnNavigationItemSelectedListener {item ->
            onNavDestinationSelected(item, navigationController)
        }
    }

    override fun getNavController() = navigationController

    override fun onSupportNavigateUp() = navigationController.navigateUp()

    override fun navigateFavouritesToNearby() = navigationController.navigate(R.id.favouritesFragment_to_nearbyFragment)

    override fun navigateFavouritesToSearch() = navigationController.navigate(R.id.favouritesFragment_to_searchFragment)

    override fun navigateLiveDataToSettings() = navigationController.navigate(R.id.liveDataFragment_to_settingsFragment)

    override fun navigateFavouritesToLiveData(serviceLocation: ServiceLocation) {
        val intent = FavouritesFragmentDirections.favouritesFragmentToLivedataFragment(
            serviceLocation.id,
            serviceLocation.name,
            serviceLocation.service,
            serviceLocation.isFavourite()
        )
        navigationController.navigate(intent)
    }

    override fun navigateNearbyToLiveData(serviceLocation: ServiceLocation) {
        val intent = NearbyFragmentDirections.nearbyFragmentToLivedataFragment(
            serviceLocation.id,
            serviceLocation.name,
            serviceLocation.service,
            serviceLocation.isFavourite()
        )
        navigationController.navigate(intent)
    }

    override fun navigateSearchToLiveData(serviceLocation: ServiceLocation) {
        val intent = SearchFragmentDirections.searchFragmentToLivedataFragment(
            serviceLocation.id,
            serviceLocation.name,
            serviceLocation.service,
            serviceLocation.isFavourite()
        )
        navigationController.navigate(intent)
    }
}
