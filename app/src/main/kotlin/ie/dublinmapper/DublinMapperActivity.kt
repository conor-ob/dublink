package ie.dublinmapper

import android.os.Bundle
import android.view.View
import androidx.navigation.NavHost
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import dagger.android.support.DaggerAppCompatActivity
import ie.dublinmapper.livedata.LiveDataFragment
import io.rtpi.api.ServiceLocation
import kotlinx.android.synthetic.main.activity_root.*

class DublinMapperActivity : DaggerAppCompatActivity(), NavHost, DublinMapperNavigator {

    private val navigationController by lazy { findNavController(R.id.navHostFragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        navigation.setupWithNavController(navigationController)
        navigation.setOnNavigationItemSelectedListener { item ->
            onNavDestinationSelected(item, navigationController)
        }
        navigationController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.newsFragment,
                R.id.nearbyFragment,
                R.id.favouritesFragment,
                R.id.searchFragment -> navigation.visibility = View.VISIBLE
                else -> navigation.visibility = View.GONE
            }
        }
    }

    override fun getNavController() = navigationController

    override fun onSupportNavigateUp() = navigationController.navigateUp()

    override fun navigateToSettings() {
        navigationController.navigate(
            R.id.settingsFragment,
            Bundle.EMPTY,
            NavOptions.Builder()
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                .build()
        )
    }

    override fun navigateToLiveData(serviceLocation: ServiceLocation) {
        navigationController.navigate(
            R.id.liveDataFragment,
            LiveDataFragment.toBundle(serviceLocation),
            NavOptions.Builder()
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                .build()
        )
    }
}
